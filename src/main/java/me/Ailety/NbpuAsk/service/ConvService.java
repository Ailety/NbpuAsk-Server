package me.Ailety.NbpuAsk.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationShare;
import me.Ailety.NbpuAsk.model.DTO.SharedConversationResponse;
import me.Ailety.NbpuAsk.model.ModelRequest;

import java.io.IOException;
import java.util.List;

@Service
public interface ConvService {

    Conversation createService(Long conversationUserId) throws IOException;

    Conversation getService(Long conversationUserId, String conversationId) throws IOException;

    SharedConversationResponse getSharedService(String shareId) throws IOException;

    List<Conversation> getAllService(Long conversationUserId) throws IOException;

    List<ConversationShare> getShareService(Long ownerUserId) throws IOException;

    void setService(Conversation conversation) throws IOException;

    void deleteService(Long conversationUserId, String conversationId) throws IOException;

    void deleteAllService(Long conversationUserId) throws IOException;

    ConversationShare shareService(Long ownerUserId, String conversationId, String requestedShareId) throws IOException;

    void cancelShareService(Long ownerUserId, String conversationId) throws IOException;

    Flux<String> runsService(ModelRequest modelRequest) throws IOException;

}
