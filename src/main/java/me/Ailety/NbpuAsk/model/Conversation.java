package me.Ailety.NbpuAsk.model;

import lombok.Data;
import me.Ailety.NbpuAsk.model.DTO.ConversationDataJson;

@Data
public class Conversation {

    private String conversationId;
    private Long conversationUserId;
    private Boolean conversationAvailable;
    private ConversationDataJson conversationData;
    private ConversationShare share;

    public Conversation() {}

    public Conversation(String conversationId, Long conversationUserId, Boolean conversationAvailable, ConversationDataJson conversationData) {
        this.conversationId = conversationId;
        this.conversationUserId = conversationUserId;
        this.conversationAvailable = conversationAvailable;
        this.conversationData = conversationData;
    }

}

