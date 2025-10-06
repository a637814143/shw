package com.example.housekeeping.security;

/**
 * Simple abstraction for hashing passwords without relying on Spring Security.
 */
public interface PasswordHasher {

    /**
     * Hash a raw password value.
     *
     * @param rawPassword the clear text password (must not be {@code null})
     * @return the hashed representation
     */
    String hash(String rawPassword);

    /**
     * Compare a raw password against a hashed value.
     *
     * @param rawPassword    the clear text password to verify
     * @param hashedPassword the stored hashed value
     * @return {@code true} if they match, otherwise {@code false}
     */
    boolean matches(String rawPassword, String hashedPassword);
}
