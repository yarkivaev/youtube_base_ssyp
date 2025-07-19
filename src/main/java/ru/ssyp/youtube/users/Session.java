package ru.ssyp.youtube.users;

import ru.ssyp.youtube.token.Token;

/**
 * Represents a session, so one account can have multiple at once.
 */
public interface Session {
    int userId();

    String username();

    Token token();
}
