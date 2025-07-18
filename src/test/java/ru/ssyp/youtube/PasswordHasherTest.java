package ru.ssyp.youtube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PasswordHasherTest {
    private PasswordHasher hasher;

    @BeforeEach
    void setUp() {
        hasher = new PasswordHasher();
    }

    @Test
    void testSalt() {
        String hash1 = hasher.hashPassword("abc");
        String hash2 = hasher.hashPassword("abc");
        Assertions.assertNotEquals(hash1, hash2);
    }

    @Test
    void testHashAndCheck() {
        String hash = hasher.hashPassword("supersecurepassword");
        Assertions.assertTrue(hasher.checkPassword(hash, "supersecurepassword"));
        Assertions.assertFalse(hasher.checkPassword(hash, "qwerty123"));
    }
}
