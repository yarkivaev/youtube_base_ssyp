package ru.ssyp.youtube.password;

public interface Password {
    String value();

    String hash();

    boolean check(String hash);
}
