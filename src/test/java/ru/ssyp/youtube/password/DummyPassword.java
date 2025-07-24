package ru.ssyp.youtube.password;

public class DummyPassword implements Password {
    private final String value;

    public DummyPassword(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String hash() {
        return value;
    }

    @Override
    public boolean check(String hash) {
        return hash.equals(value);
    }
}
