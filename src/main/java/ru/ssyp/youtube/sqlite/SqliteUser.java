package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.User;

public class SqliteUser implements User {
    private final String name;

    public SqliteUser(String name) {
        this.name = name;
    }

    @Override
    public String uniqueName() {
        return name;
    }
}
