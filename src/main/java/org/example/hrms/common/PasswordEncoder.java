package org.example.hrms.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具：BCrypt 加密 + 向后兼容明文密码的渐进式升级。
 * 若数据库中存的是明文（不以 $2 开头），首次登录成功后自动升级为 BCrypt。
 */
public class PasswordEncoder {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /** 加密明文 */
    public static String encode(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 验证密码：若存储的密码为 BCrypt 格式则 BCrypt 比对；
     * 否则回退到明文比对（兼容历史种子数据）。
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }
        if (!isBCrypt(encodedPassword)) {
            return rawPassword.equals(encodedPassword);
        }
        return ENCODER.matches(rawPassword, encodedPassword);
    }

    /** 判断密码是否为 BCrypt 哈希（需升级为 BCrypt） */
    public static boolean isBCrypt(String encoded) {
        return encoded != null && encoded.startsWith("$2");
    }
}
