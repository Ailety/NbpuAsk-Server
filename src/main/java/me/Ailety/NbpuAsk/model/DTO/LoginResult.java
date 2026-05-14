package me.Ailety.NbpuAsk.model.DTO;

import lombok.Data;

@Data
public class LoginResult {

    private String userId;
    private String username;
    private UserDataJson userData;
    private String token;

    public LoginResult(String userId, String username, UserDataJson userData, String token) {
        this.userId = userId;
        this.username = username;
        this.userData = userData;
        this.token = token;
    }
}
