package ru.ssyp.youtube.token;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenGenRandomB64Test {
    private TokenGen tokenGen;

    @BeforeEach
    void setUp() {
        tokenGen = new TokenGenRandomB64(20);
    }

    @Test
    void testUnique() {
        Token token1 = tokenGen.token();
        Token token2 = tokenGen.token();

        Assertions.assertNotEquals(token1, token2);
    }
}
