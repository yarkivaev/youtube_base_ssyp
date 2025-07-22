package ru.ssyp.youtube.token;

/**
 * A strategy for generating tokens.
 */
public interface TokenGen {
    /**
     * Generate a new unique token.
     */
    Token token();
}
