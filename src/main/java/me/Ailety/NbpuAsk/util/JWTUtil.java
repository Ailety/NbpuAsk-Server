package me.Ailety.NbpuAsk.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import me.Ailety.NbpuAsk.model.DTO.TokenValidationResult;
import me.Ailety.NbpuAsk.model.User;

import java.util.Calendar;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JWTUtil {

    /**
     * 生成token header.payload.signature
     */
    private String issuer;
    private String secretKey;
    private Integer expiredTime;

    public String generateToken(String username) {

        Calendar issuedInstance = Calendar.getInstance();
        Calendar expiresInstance = Calendar.getInstance();
        expiresInstance.add(Calendar.MINUTE, expiredTime);

        // 创建token
        return JWT.create()
                .withIssuer(issuer) // 签发者
                .withSubject(username) // 用户唯一标识
                .withIssuedAt(issuedInstance.getTime()) // 签发时间
                .withExpiresAt(expiresInstance.getTime()) // 过期时间
                .sign(Algorithm.HMAC256(secretKey)); // 加密算法
    }

    /**
     * 验证token 合法性
     */
    public TokenValidationResult verifyToken(String token, User user) {
        TokenValidationResult result = verifyToken(token);
        if (result.getCode() != 200 && result.getCode() != 401) {
            return result;
        }

        if (user == null
                || user.getUsername() == null
                || result.getUsername() == null
                || !result.getUsername().equals(user.getUsername())) {
            return new TokenValidationResult(403, "令牌不匹配", null, result.getUsername());
        }

        return result;
    }

    public TokenValidationResult verifyToken(String token) {
        try {

            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();

            DecodedJWT jwt = verifier.verify(token);

            String tokenUsername = jwt.getSubject();

            return new TokenValidationResult(200, "令牌有效", null, tokenUsername); // 验证成功

        } catch (TokenExpiredException ex) {

            String tokenUsername = JWT.decode(token).getSubject();
            String newToken = generateToken(tokenUsername);
            return new TokenValidationResult(401, "令牌过期", newToken, tokenUsername); // 过期Token

        } catch (JWTVerificationException ex) {

            return new TokenValidationResult(402, "令牌无效", null, null); // 无效Token

        }
    }

}
