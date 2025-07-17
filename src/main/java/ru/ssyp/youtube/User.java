package ru.ssyp.youtube;

/**
 * Represents a session, so one account can have multiple at once.
 */
public interface User {
    int id();

    String username();

    String token();
}
