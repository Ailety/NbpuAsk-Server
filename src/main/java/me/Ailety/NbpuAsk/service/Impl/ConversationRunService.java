package me.Ailety.NbpuAsk.service.Impl;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import me.Ailety.NbpuAsk.dao.ConversationDao;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationTitleStatus;
import me.Ailety.NbpuAsk.model.Message;
import me.Ailety.NbpuAsk.model.ModelRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@ConfigurationProperties(prefix = "model")
public class ConversationRunService {

    private final ConversationDao conversationDao;
    private final ConversationTitleService conversationTitleService;

    private String appId;

    public ConversationRunService(ConversationDao conversationDao,
                                  ConversationTitleService conversationTitleService) {
        this.conversationDao = conversationDao;
        this.conversationTitleService = conversationTitleService;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Flux<String> run(ModelRequest modelRequest) {
        Conversation conversation = null;
        Message pendingModelMessage = null;
        boolean shouldAutoGenerateTitle = false;
        long thinkingStartTime = System.currentTimeMillis();

        if (modelRequest.getUserId() != null && modelRequest.getConversation_id() != null) {
            conversation = conversationDao.getConversation(modelRequest.getUserId(), modelRequest.getConversation_id());
            if (conversation != null) {
                shouldAutoGenerateTitle = shouldAutoGenerateTitle(conversation);
                pendingModelMessage = appendPendingMessages(conversation, modelRequest.getQuery(), thinkingStartTime);
                if (pendingModelMessage == null) {
                    return Flux.error(new IllegalStateException("当前对话仍有未完成的模型响应，请等待其完成后再发送新问题"));
                }

                if (shouldAutoGenerateTitle) {
                    conversation.getConversationData().setTitleStatus(ConversationTitleStatus.GENERATING);
                }
                conversationDao.setConversation(conversation);
            }
        }

        final Conversation finalConversation = conversation;
        final Message finalPendingModelMessage = pendingModelMessage;
        final boolean finalShouldAutoGenerateTitle = shouldAutoGenerateTitle;
        final long finalThinkingStartTime = thinkingStartTime;
        final Long finalUserId = modelRequest.getUserId();
        final String finalConversationId = modelRequest.getConversation_id();
        final String finalQuery = modelRequest.getQuery();
        StringBuilder modelResponseBuilder = new StringBuilder();

        ApplicationParam param = ApplicationParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .appId(appId)
                .prompt(modelRequest.getQuery())
                .sessionId(modelRequest.getConversation_id())
                .incrementalOutput(true)
                .build();

        try {
            Flowable<ApplicationResult> flowable = new Application().streamCall(param);

            return Flux.from(flowable)
                    .map(result -> extractChunk(result, modelResponseBuilder))
                    .doFinally(signalType -> completePendingMessage(
                            finalConversation,
                            finalPendingModelMessage,
                            finalThinkingStartTime,
                            modelResponseBuilder.toString(),
                            finalShouldAutoGenerateTitle,
                            signalType == SignalType.ON_COMPLETE,
                            finalUserId,
                            finalConversationId,
                            finalQuery
                    ))
                    .doOnError(e -> log.error("❌ [异常] 百炼大模型流式调用出错: {}", e.getMessage()));
        } catch (Exception e) {
            log.error("❌ [异常] 构建流式调用失败: {}", e.getMessage());
            return Flux.error(e);
        }
    }

    private Message appendPendingMessages(Conversation conversation, String query, long thinkingStartTime) {
        if (conversation.getConversationData().getMessages() == null) {
            conversation.getConversationData().setMessages(new ArrayList<>());
        }

        List<Message> messages = conversation.getConversationData().getMessages();
        if (hasPendingModelMessage(messages)) {
            return null;
        }

        conversation.getConversationData().setTimestamp(String.valueOf(thinkingStartTime));

        Message userMessage = new Message();
        userMessage.setSender("user");
        userMessage.setMessage(query);
        messages.add(userMessage);

        Message pendingModelMessage = new Message();
        pendingModelMessage.setSender("model");
        pendingModelMessage.setMessage("");
        pendingModelMessage.setThinking(true);
        pendingModelMessage.setThinkingStartTime(thinkingStartTime);
        messages.add(pendingModelMessage);
        return pendingModelMessage;
    }

    private String extractChunk(ApplicationResult result, StringBuilder modelResponseBuilder) {
        if (result.getOutput() != null && result.getOutput().getText() != null) {
            String chunk = result.getOutput().getText();
            modelResponseBuilder.append(chunk);
            return chunk;
        }

        return "";
    }

    private void completePendingMessage(Conversation conversation,
                                        Message pendingModelMessage,
                                        long thinkingStartTime,
                                        String fullResponse,
                                        boolean shouldAutoGenerateTitle,
                                        boolean modelRunCompleted,
                                        Long userId,
                                        String conversationId,
                                        String query) {
        if (conversation == null || pendingModelMessage == null) {
            return;
        }

        long thinkingFinishedTime = System.currentTimeMillis();
        long durationSeconds = Math.max(0L, (thinkingFinishedTime - thinkingStartTime + 999L) / 1000L);

        pendingModelMessage.setMessage(normalizeMarkdown(fullResponse));
        pendingModelMessage.setThinking(false);
        pendingModelMessage.setThinkingFinishedTime(thinkingFinishedTime);
        pendingModelMessage.setThinkingDurationSeconds(durationSeconds);
        conversation.getConversationData().setTimestamp(String.valueOf(thinkingFinishedTime));
        preserveLatestTitleState(conversation, userId, conversationId);
        conversationDao.setConversation(conversation);

        if (shouldAutoGenerateTitle && modelRunCompleted) {
            conversationTitleService.generateTitleAfterFirstResponseAsync(
                    userId,
                    conversationId,
                    query,
                    pendingModelMessage.getMessage()
            );
        } else if (shouldAutoGenerateTitle) {
            conversationTitleService.markTitleFailedIfStillGenerating(userId, conversationId);
        }
    }

    private String normalizeMarkdown(String content) {
        String normalizedContent = content.replaceAll("\\^\\[(\\d+)\\]\\^", "<sup class=\"footnote-ref\">[$1]</sup>");
        return normalizedContent.replaceAll("\\*\\*\\s+([^*]+?)\\s+\\*\\*", "**$1**");
    }

    private void preserveLatestTitleState(Conversation conversation, Long userId, String conversationId) {
        if (conversation == null
                || conversation.getConversationData() == null
                || userId == null
                || conversationId == null) {
            return;
        }

        Conversation latestConversation = conversationDao.getConversation(userId, conversationId);
        if (latestConversation == null || latestConversation.getConversationData() == null) {
            return;
        }

        conversation.getConversationData().setTitle(latestConversation.getConversationData().getTitle());
        conversation.getConversationData().setTitleStatus(latestConversation.getConversationData().getTitleStatus());
    }

    private boolean hasPendingModelMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return false;
        }

        for (int i = messages.size() - 1; i >= 0; i--) {
            Message message = messages.get(i);
            if ("model".equals(message.getSender())) {
                return Boolean.TRUE.equals(message.getThinking());
            }
            if ("user".equals(message.getSender())) {
                return false;
            }
        }

        return false;
    }

    private boolean shouldAutoGenerateTitle(Conversation conversation) {
        if (conversation == null || conversation.getConversationData() == null) {
            return false;
        }

        List<Message> messages = conversation.getConversationData().getMessages();
        return (messages == null || messages.isEmpty())
                && conversationTitleService.isDefaultTitleStatus(
                conversation.getConversationData().getTitleStatus()
        );
    }
}
