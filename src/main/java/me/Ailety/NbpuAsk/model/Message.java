package me.Ailety.NbpuAsk.model;

import lombok.Data;

@Data
public class Message {

    private String sender;
    private String message;
    private Boolean thinking;
    private Long thinkingStartTime;
    private Long thinkingFinishedTime;
    private Long thinkingDurationSeconds;

    public Message() {}
}
