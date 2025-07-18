package ru.ssyp.youtube;

import java.security.SecureRandom;
import java.util.Base64;

public class Token {
    public final String value;

    public Token() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        value = encoder.encodeToString(bytes);
    }

    public Token(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token[" + value + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Token)) {
            return false;
        }

        return value.equals(((Token) obj).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
