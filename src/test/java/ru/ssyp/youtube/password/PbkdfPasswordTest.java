package ru.ssyp.youtube.password;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PbkdfPasswordTest {
    @Test
    void testValue() {
        Password pass = new PbkdfPassword("123");
        Assertions.assertEquals("123", pass.value());
    }

    @Test
    void testSalt() {
        Password pass = new PbkdfPassword("abc");

        String hash1 = pass.hash();
        String hash2 = pass.hash();

        Assertions.assertNotEquals(hash1, hash2);
    }

    @Test
    void testHashAndCheck() {
        Password pass = new PbkdfPassword("supersecurepassword");
        String hash = pass.hash();

        Password wrongpass = new PbkdfPassword("qwerty123");
        String wronghash = wrongpass.hash();

        Assertions.assertTrue(pass.check(hash));
        Assertions.assertFalse(pass.check(wronghash));
    }
}
