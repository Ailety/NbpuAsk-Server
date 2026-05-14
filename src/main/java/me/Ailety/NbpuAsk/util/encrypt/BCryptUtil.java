package me.Ailety.NbpuAsk.util.encrypt;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {

    // 生成哈希密码
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // 验证哈希密码
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
