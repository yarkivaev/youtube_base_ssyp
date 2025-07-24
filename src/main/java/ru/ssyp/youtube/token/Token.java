package ru.ssyp.youtube.token;

import ru.ssyp.youtube.ProtocolValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Token implements ProtocolValue {
    public final String value;

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

    @Override
    public InputStream rawContent() {
        return new ByteArrayInputStream(value.getBytes());
    }
}
