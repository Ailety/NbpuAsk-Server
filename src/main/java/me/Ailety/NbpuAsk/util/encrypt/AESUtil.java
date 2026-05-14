package me.Ailety.NbpuAsk.util.encrypt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class AESUtil {

    private String aesSecretKey;

    // 加密（返回 Base64(IV + 密文)）
    public String encrypt(String plainText) throws Exception {

        // 生成随机IV（必须16字节）
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 构建密钥
        SecretKey secretKey = new SecretKeySpec(aesSecretKey.getBytes(), "AES");

        // 加密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 拼接IV和密文
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // 解密（从Base64数据提取IV和密文）
    public String decrypt(String encryptedText) throws Exception {
        // 解码Base64
        byte[] combined = Base64.getDecoder().decode(encryptedText);

        // 提取IV（前16字节）
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // 提取密文（剩余字节）
        byte[] encryptedBytes = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

        // 构建密钥
        SecretKey secretKey = new SecretKeySpec(aesSecretKey.getBytes(), "AES");

        // 解密
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}