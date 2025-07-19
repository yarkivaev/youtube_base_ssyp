package ru.ssyp.youtube.token;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Generates random base64 tokens.
 */
public class TokenGenRandomB64 implements TokenGen {
    private final SecureRandom random = new SecureRandom();
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    public final int size;

    public TokenGenRandomB64(int size) {
        this.size = size;
    }

    @Override
    public Token token() {
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);

        return new Token(encoder.encodeToString(bytes));
    }
}
