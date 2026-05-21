package me.Ailety.NbpuAsk.service.Impl;

import me.Ailety.NbpuAsk.dao.ConversationDao;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationTitleStatus;
import me.Ailety.NbpuAsk.model.DTO.ConversationDataJson;
import me.Ailety.NbpuAsk.model.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ConversationCrudService {

    private final ConversationDao conversationDao;

    public ConversationCrudService(ConversationDao conversationDao) {
        this.conversationDao = conversationDao;
    }

    public Conversation createConversation(Long conversationUserId) {
        String localSessionId = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());

        ConversationDataJson conversationData = new ConversationDataJson(
                "新对话" + timestamp.substring(timestamp.length() - 4),
                timestamp,
                timestamp,
                new ArrayList<Message>()
        );
        conversationData.setTitleStatus(ConversationTitleStatus.DEFAULT);

        Conversation conversation = new Conversation(
                localSessionId,
                conversationUserId,
                true,
                conversationData
        );
        conversationDao.createConversation(conversation);
        return conversation;
    }

    public Conversation getConversation(Long conversationUserId, String conversationId) {
        return conversationDao.getConversation(conversationUserId, conversationId);
    }

    public List<Conversation> getConversations(Long conversationUserId) {
        return conversationDao.getConversations(conversationUserId);
    }

    public void saveConversation(Conversation conversation) {
        markManualTitleIfChanged(conversation);
        conversationDao.setConversation(conversation);
    }

    public void deleteConversation(Long conversationUserId, String conversationId) {
        conversationDao.deleteConversation(conversationUserId, conversationId);
    }

    public void deleteConversations(Long conversationUserId) {
        conversationDao.deleteConversations(conversationUserId);
    }

    private void markManualTitleIfChanged(Conversation conversation) {
        if (conversation == null
                || conversation.getConversationUserId() == null
                || conversation.getConversationId() == null
                || conversation.getConversationData() == null) {
            return;
        }

        Conversation persistedConversation = conversationDao.getConversation(
                conversation.getConversationUserId(),
                conversation.getConversationId()
        );
        if (persistedConversation == null || persistedConversation.getConversationData() == null) {
            return;
        }

        String currentTitle = persistedConversation.getConversationData().getTitle();
        String nextTitle = conversation.getConversationData().getTitle();
        if (!Objects.equals(currentTitle, nextTitle)) {
            conversation.getConversationData().setTitleStatus(ConversationTitleStatus.MANUAL);
        }
    }
}
