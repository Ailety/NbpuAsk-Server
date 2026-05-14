package me.Ailety.NbpuAsk.model.DTO;

import lombok.Data;

@Data
public class TokenValidationResult {

    private int code;        // 状态码(200=有效, 401=过期, 402=失效, 403=用户信息不匹配)
    private String message;  // 描述信息
    private String newToken; // 刷新后的 Token(仅当 code=401 时存在)
    private String username; // Token 中解析出的用户名

    public TokenValidationResult(int code, String message, String newToken) {
        this(code, message, newToken, null);
    }

    public TokenValidationResult(int code, String message, String newToken, String username) {
        this.code = code;
        this.message = message;
        this.newToken = newToken;
        this.username = username;
    }
}
