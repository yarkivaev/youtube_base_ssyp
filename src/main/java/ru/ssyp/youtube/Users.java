package ru.ssyp.youtube;

import ru.ssyp.youtube.token.Token;

public interface Users {
    /** 
     * Register new user.
     *
     * @param name Name is unique.
     * @return token
     */
    Token addUser(String name, String password);

    /**
     * Log in as existing user.
     *
     * @param name Name is unique.
     * @return token
     */
    Token login(String name, String password);

    /**
     * Get session info by token.
     * @return Session or null if token is invalid.
     */
    Session getSession(Token token);
}
