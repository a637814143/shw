package com.example.housekeeping.util;

import org.springframework.stereotype.Component;

/**
 * 编码旧版账号密码的工具。
 */
@Component
public class LegacyPasswordEncoder {

    public String encode(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("密码不能为空");
        }
        String first = java.util.Base64.getEncoder()
            .encodeToString(rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String second = java.util.Base64.getEncoder()
            .encodeToString(first.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return rot13(second);
    }

    private String rot13(String input) {
        StringBuilder builder = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                builder.append((char) ((c - 'a' + 13) % 26 + 'a'));
            } else if (c >= 'A' && c <= 'Z') {
                builder.append((char) ((c - 'A' + 13) % 26 + 'A'));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
