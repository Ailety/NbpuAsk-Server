package me.Ailety.NbpuAsk.model.DTO;

import lombok.Data;
import me.Ailety.NbpuAsk.model.Conversation;
import me.Ailety.NbpuAsk.model.ConversationShare;

@Data
public class SharedConversationResponse {

    private Conversation conversation;
    private ConversationShare share;
    private ShareOwnerInfo owner;

    public SharedConversationResponse() {}

    public SharedConversationResponse(Conversation conversation, ConversationShare share, ShareOwnerInfo owner) {
        this.conversation = conversation;
        this.share = share;
        this.owner = owner;
    }
}
