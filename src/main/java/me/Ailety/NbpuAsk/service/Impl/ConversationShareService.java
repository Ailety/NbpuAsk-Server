package me.Ailety.NbpuAsk.service.Impl;

import me.Ailety.NbpuAsk.dao.ConversationDao;
import me.Ailety.NbpuAsk.dao.ConversationShareDao;
import me.Ailety.NbpuAsk.dao.UserDao;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationShare;
import me.Ailety.NbpuAsk.model.DTO.ShareOwnerInfo;
import me.Ailety.NbpuAsk.model.DTO.SharedConversationResponse;
import me.Ailety.NbpuAsk.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationShareService {

    private final ConversationDao conversationDao;
    private final ConversationShareDao conversationShareDao;
    private final UserDao userDao;

    public ConversationShareService(ConversationDao conversationDao,
                                    ConversationShareDao conversationShareDao,
                                    UserDao userDao) {
        this.conversationDao = conversationDao;
        this.conversationShareDao = conversationShareDao;
        this.userDao = userDao;
    }

    public SharedConversationResponse getSharedConversation(String shareId) {
        ConversationShare share = conversationShareDao.getActiveShareByShareId(shareId);
        if (share == null) {
            return null;
        }

        Conversation conversation = conversationDao.getConversation(share.getOwnerUserId(), share.getConversationId());
        if (conversation == null || !Boolean.TRUE.equals(conversation.getConversationAvailable())) {
            return null;
        }

        String lastVisitedTime = String.valueOf(System.currentTimeMillis());
        conversationShareDao.incrementVisitCount(share.getShareId(), lastVisitedTime);
        share = conversationShareDao.getActiveShareByShareId(share.getShareId());
        conversation.setShare(share);

        User owner = userDao.findByUserId(share.getOwnerUserId());
        return new SharedConversationResponse(conversation, share, new ShareOwnerInfo(owner));
    }

    public List<ConversationShare> getActiveShares(Long ownerUserId) {
        return conversationShareDao.getActiveSharesByOwnerUserId(ownerUserId);
    }

    public ConversationShare shareConversation(Long ownerUserId, String conversationId, String requestedShareId) {
        Conversation conversation = conversationDao.getConversation(ownerUserId, conversationId);
        if (conversation == null || !Boolean.TRUE.equals(conversation.getConversationAvailable())) {
            return null;
        }

        ConversationShare activeShare = conversationShareDao.getActiveShareByConversationId(conversationId, ownerUserId);
        if (activeShare != null) {
            return activeShare;
        }

        ConversationShare share = new ConversationShare(
                normalizeShareId(requestedShareId),
                conversationId,
                ownerUserId,
                String.valueOf(System.currentTimeMillis())
        );
        conversationShareDao.upsertShare(share);
        return conversationShareDao.getActiveShareByConversationId(conversationId, ownerUserId);
    }

    public void cancelShare(Long ownerUserId, String conversationId) {
        conversationShareDao.cancelShare(conversationId, ownerUserId);
    }

    public void deleteSharesByConversation(Long ownerUserId, String conversationId) {
        conversationShareDao.deleteSharesByConversation(conversationId, ownerUserId);
    }

    public void deleteSharesByOwner(Long ownerUserId) {
        conversationShareDao.deleteSharesByOwnerUserId(ownerUserId);
    }

    private String normalizeShareId(String requestedShareId) {
        if (requestedShareId != null && requestedShareId.matches("^[A-Za-z0-9_-]{16,80}$")) {
            return requestedShareId;
        }

        return UUID.randomUUID().toString().replace("-", "");
    }
}
