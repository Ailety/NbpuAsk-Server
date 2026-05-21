package me.Ailety.NbpuAsk.service.Impl;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import lombok.extern.slf4j.Slf4j;
import me.Ailety.NbpuAsk.dao.ConversationDao;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationTitleStatus;
import me.Ailety.NbpuAsk.model.DTO.ConversationDataJson;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@ConfigurationProperties(prefix = "model")
public class ConversationTitleService {

    private static final int MAX_TITLE_LENGTH = 10;

    private final ConversationDao conversationDao;

    private String appId;

    public ConversationTitleService(ConversationDao conversationDao) {
        this.conversationDao = conversationDao;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isDefaultTitleStatus(String titleStatus) {
        return titleStatus == null
                || titleStatus.trim().isEmpty()
                || ConversationTitleStatus.DEFAULT.equals(titleStatus)
                || ConversationTitleStatus.FAILED.equals(titleStatus);
    }

    public void generateTitleAfterFirstResponseAsync(Long userId,
                                                     String conversationId,
                                                     String userQuestion,
                                                     String modelAnswer) {
        CompletableFuture.runAsync(() -> generateTitleAfterFirstResponse(
                userId,
                conversationId,
                userQuestion,
                modelAnswer
        ));
    }

    private void generateTitleAfterFirstResponse(Long userId,
                                                 String conversationId,
                                                 String userQuestion,
                                                 String modelAnswer) {
        if (userId == null || conversationId == null || isBlank(userQuestion) || isBlank(modelAnswer)) {
            markTitleFailedIfStillGenerating(userId, conversationId);
            return;
        }

        Conversation conversation = conversationDao.getConversation(userId, conversationId);
        if (!isTitleGenerating(conversation)) {
            return;
        }

        try {
            String generatedTitle = requestTitle(userQuestion, modelAnswer);
            if (isBlank(generatedTitle)) {
                markTitleFailedIfStillGenerating(userId, conversationId);
                return;
            }

            Conversation latestConversation = conversationDao.getConversation(userId, conversationId);
            if (!isTitleGenerating(latestConversation)) {
                return;
            }

            ConversationDataJson conversationData = latestConversation.getConversationData();
            conversationData.setTitle(generatedTitle);
            conversationData.setTitleStatus(ConversationTitleStatus.GENERATED);
            conversationDao.setConversation(latestConversation);
        } catch (Exception e) {
            log.warn("自动生成对话标题失败: conversationId={}, reason={}", conversationId, e.getMessage());
            markTitleFailedIfStillGenerating(userId, conversationId);
        }
    }

    private String requestTitle(String userQuestion, String modelAnswer) throws Exception {
        ApplicationParam param = ApplicationParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .appId(appId)
                .prompt(buildTitlePrompt(userQuestion, modelAnswer))
                .temperature(0.2f)
                .incrementalOutput(false)
                .build();

        ApplicationResult result = new Application().call(param);
        if (result == null || result.getOutput() == null || result.getOutput().getText() == null) {
            return "";
        }

        return sanitizeTitle(result.getOutput().getText());
    }

    private String buildTitlePrompt(String userQuestion, String modelAnswer) {
        return "请根据用户问题和助手回答，为这段对话生成一个中文短标题。\n"
                + "要求：\n"
                + "1. 不超过10个汉字或字符。\n"
                + "2. 不要使用标点符号。\n"
                + "3. 不要解释，不要输出JSON，不要添加引号。\n"
                + "4. 只输出标题本身。\n\n"
                + "用户问题：\n"
                + userQuestion
                + "\n\n助手回答：\n"
                + modelAnswer;
    }

    private String sanitizeTitle(String title) {
        if (title == null) {
            return "";
        }

        String sanitizedTitle = title
                .replaceAll("(?s)```.*?```", "")
                .replaceAll("[\\r\\n\\t]+", "")
                .replaceAll("^\\s*(标题|对话标题)\\s*[:：]\\s*", "")
                .replaceAll("[\"'“”‘’`]", "")
                .replaceAll("[\\p{Punct}，。！？；：“”‘’（）【】《》、…·\\s]+", "")
                .trim();

        if (sanitizedTitle.codePointCount(0, sanitizedTitle.length()) > MAX_TITLE_LENGTH) {
            sanitizedTitle = sanitizedTitle.substring(0, sanitizedTitle.offsetByCodePoints(0, MAX_TITLE_LENGTH));
        }

        return sanitizedTitle;
    }

    private boolean isTitleGenerating(Conversation conversation) {
        return conversation != null
                && conversation.getConversationData() != null
                && ConversationTitleStatus.GENERATING.equals(conversation.getConversationData().getTitleStatus());
    }

    public void markTitleFailedIfStillGenerating(Long userId, String conversationId) {
        if (userId == null || conversationId == null) {
            return;
        }

        Conversation conversation = conversationDao.getConversation(userId, conversationId);
        if (!isTitleGenerating(conversation)) {
            return;
        }

        conversation.getConversationData().setTitleStatus(ConversationTitleStatus.FAILED);
        conversationDao.setConversation(conversation);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
