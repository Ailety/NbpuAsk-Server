package me.Ailety.NbpuAsk.model;

import lombok.Data;

@Data
public class ConversationShare {

    private String shareId;
    private String conversationId;
    private Long ownerUserId;
    private String shareCreatedTime;
    private Boolean shareAvailable;
    private Long visitCount;
    private String lastVisitedTime;

    public ConversationShare() {}

    public ConversationShare(String shareId, String conversationId, Long ownerUserId, String shareCreatedTime) {
        this.shareId = shareId;
        this.conversationId = conversationId;
        this.ownerUserId = ownerUserId;
        this.shareCreatedTime = shareCreatedTime;
        this.shareAvailable = true;
        this.visitCount = 0L;
    }
}
