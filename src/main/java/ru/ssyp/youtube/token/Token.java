package ru.ssyp.youtube.token;

public class Token {
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
}
