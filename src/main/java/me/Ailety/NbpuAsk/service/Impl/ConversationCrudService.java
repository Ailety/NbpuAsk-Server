package me.Ailety.NbpuAsk.service.Impl;

import me.Ailety.NbpuAsk.dao.ConversationDao;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.DTO.ConversationDataJson;
import me.Ailety.NbpuAsk.model.Message;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        conversationDao.setConversation(conversation);
    }

    public void deleteConversation(Long conversationUserId, String conversationId) {
        conversationDao.deleteConversation(conversationUserId, conversationId);
    }

    public void deleteConversations(Long conversationUserId) {
        conversationDao.deleteConversations(conversationUserId);
    }
}
