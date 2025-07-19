package ru.ssyp.youtube;

import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.token.Token;

/**
 * A user account and session manager.
 */
public interface Users {
    /** 
     * Register new user.
     *
     * @param name Name is unique.
     * @return token
     */
    Token addUser(String name, Password password);

    /**
     * Log in as existing user.
     *
     * @param name Name is unique.
     * @return token
     */
    Token login(String name, Password password);

    /**
     * Get session info by token.
     * @return Session or null if token is invalid.
     */
    Session getSession(Token token);
}
