package ru.ssyp.youtube.password;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DummyPasswordTest {
    @Test
    void test() {
        Password pass = new DummyPassword("123");

        Assertions.assertEquals("123", pass.value());
        Assertions.assertEquals("123", pass.hash());
        Assertions.assertTrue(pass.check("123"));
    }
}
