package me.Ailety.NbpuAsk.model.DTO;

import lombok.Data;
import me.Ailety.NbpuAsk.model.Message;

import java.util.List;

@Data
public class ConversationDataJson {

    private String title;
    private String createdTime;
    private String timestamp;
    private List<Message> messages;

    public ConversationDataJson() {}

    public ConversationDataJson(String title, String createdTime, String timestamp, List<Message> messages) {
        this.title = title;
        this.createdTime = createdTime;
        this.timestamp = timestamp;
        this.messages = messages;
    }
}
