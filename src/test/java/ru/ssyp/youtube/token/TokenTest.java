package ru.ssyp.youtube.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TokenTest {
    @Test
    void testFromValue() {
        Token token = new Token("test_token");
        Assertions.assertEquals("test_token", token.value);
    }

    @Test
    void testToString() {
        Token token = new Token("test_token");
        Assertions.assertEquals("Token[test_token]", token.toString());
    }

    @Test
    void testEquals() {
        Token token1 = new Token("abc");
        Token token2 = new Token(token1.value);
        Token token3 = new Token("123");

        Assertions.assertEquals(token1, token2);
        Assertions.assertNotEquals(token1, token3);
        Assertions.assertNotEquals(token1, new Object());
    }

    @Test
    void testHashCode() {
        Token token = new Token("hash me!");
        Assertions.assertEquals(token.value.hashCode(), token.hashCode());
    }
}
