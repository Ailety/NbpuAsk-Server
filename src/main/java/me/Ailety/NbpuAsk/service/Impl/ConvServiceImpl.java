package me.Ailety.NbpuAsk.service.Impl;

import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationShare;
import me.Ailety.NbpuAsk.model.DTO.SharedConversationResponse;
import me.Ailety.NbpuAsk.model.ModelRequest;
import me.Ailety.NbpuAsk.service.ConvService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class ConvServiceImpl implements ConvService {

    private final ConversationCrudService conversationCrudService;
    private final ConversationShareService conversationShareService;
    private final ConversationRunService conversationRunService;

    public ConvServiceImpl(ConversationCrudService conversationCrudService,
                           ConversationShareService conversationShareService,
                           ConversationRunService conversationRunService) {
        this.conversationCrudService = conversationCrudService;
        this.conversationShareService = conversationShareService;
        this.conversationRunService = conversationRunService;
    }

    @Override
    public Conversation createService(Long conversationUserId) {
        return conversationCrudService.createConversation(conversationUserId);
    }

    @Override
    public Conversation getService(Long conversationUserId, String conversationId) {
        return conversationCrudService.getConversation(conversationUserId, conversationId);
    }

    @Override
    public SharedConversationResponse getSharedService(String shareId) {
        return conversationShareService.getSharedConversation(shareId);
    }

    @Override
    public List<Conversation> getAllService(Long conversationUserId) {
        return conversationCrudService.getConversations(conversationUserId);
    }

    @Override
    public List<ConversationShare> getShareService(Long ownerUserId) {
        return conversationShareService.getActiveShares(ownerUserId);
    }

    @Override
    public void setService(Conversation conversation) {
        conversationCrudService.saveConversation(conversation);
    }

    @Override
    @Transactional
    public void deleteService(Long conversationUserId, String conversationId) {
        conversationShareService.deleteSharesByConversation(conversationUserId, conversationId);
        conversationCrudService.deleteConversation(conversationUserId, conversationId);
    }

    @Override
    @Transactional
    public void deleteAllService(Long conversationUserId) {
        conversationShareService.deleteSharesByOwner(conversationUserId);
        conversationCrudService.deleteConversations(conversationUserId);
    }

    @Override
    public ConversationShare shareService(Long ownerUserId, String conversationId, String requestedShareId) {
        return conversationShareService.shareConversation(ownerUserId, conversationId, requestedShareId);
    }

    @Override
    public void cancelShareService(Long ownerUserId, String conversationId) {
        conversationShareService.cancelShare(ownerUserId, conversationId);
    }

    @Override
    public Flux<String> runsService(ModelRequest modelRequest) {
        return conversationRunService.run(modelRequest);
    }
}
