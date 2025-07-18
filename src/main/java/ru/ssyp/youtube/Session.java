package ru.ssyp.youtube;

/**
 * Represents a session, so one account can have multiple at once.
 */
public interface Session {
    int id();

    String username();

    Token token();
}
