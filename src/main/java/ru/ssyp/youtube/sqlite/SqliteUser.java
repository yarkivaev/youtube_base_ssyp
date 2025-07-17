package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.User;

public class SqliteUser implements User {
    private final String username;
    private final String token;

    public SqliteUser(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String token() {
        return token;
    }
}
