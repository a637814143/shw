package com.example.housekeeping.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * Password hashing implementation based on SHA-256. This keeps the sample
 * application self-contained without bringing Spring Security as a dependency.
 */
public class Sha256PasswordHasher implements PasswordHasher {

    private static final String ALGORITHM = "SHA-256";

    @Override
    public String hash(String rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword");
        MessageDigest digest = createDigest();
        byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashed);
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        return hash(rawPassword).equals(hashedPassword);
    }

    private MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is not available", ex);
        }
    }
}
