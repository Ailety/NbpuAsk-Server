package me.Ailety.NbpuAsk.model;

import me.Ailety.NbpuAsk.model.DTO.UserDataJson;
import lombok.Data;

@Data
public class User {

    private Long userId;
    private String username;
    private String password;
    private UserDataJson userData;

    public User() {}

    public User(String username) {
        this.username = username;
    }

}