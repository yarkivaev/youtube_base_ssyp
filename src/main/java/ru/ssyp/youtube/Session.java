package ru.ssyp.youtube;

import ru.ssyp.youtube.token.Token;

/**
 * Represents a session, so one account can have multiple at once.
 */
public interface Session {
    int id();

    String username();

    Token token();
}
