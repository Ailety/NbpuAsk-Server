package me.Ailety.NbpuAsk.model;

import lombok.Data;

@Data
public class ModelRequest {

    private String query;
    private String conversation_id;
    private String sessionId;
    private Long userId;

    public ModelRequest() {}

}
