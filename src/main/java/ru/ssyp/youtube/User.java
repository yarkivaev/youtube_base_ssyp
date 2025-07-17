package ru.ssyp.youtube;

/**
 * Represents a session, so one account can have multiple at once.
 */
public interface User {
    String username();

    String token();
}
