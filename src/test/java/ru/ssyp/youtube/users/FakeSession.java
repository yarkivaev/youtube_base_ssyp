package ru.ssyp.youtube.users;

import ru.ssyp.youtube.token.Token;

public class FakeSession implements Session {
    private final int userId;
    private final String username;
    private final Token token;

    public FakeSession(int userId, String username, Token token) {
        this.userId = userId;
        this.username = username;
        this.token = token;
    }

    @Override
    public int userId() {
        return userId;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public Token token() {
        return token;
    }
}
