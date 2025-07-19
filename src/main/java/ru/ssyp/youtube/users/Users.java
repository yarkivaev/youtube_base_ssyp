package ru.ssyp.youtube.users;

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
     * @throws InvalidUsernameException if username is empty or contains invalid chars.
     * @throws InvalidPasswordException if password is empty.
     * @throws UsernameTakenException if username is already taken.
     */
    Token addUser(String name, Password password) throws InvalidUsernameException, InvalidPasswordException, UsernameTakenException;

    /**
     * Log in as existing user.
     *
     * @param name Name is unique.
     * @return token
     * @throws InvalidUsernameException if no such user exists.
     * @throws InvalidPasswordException if password is incorrect.
     */
    Token login(String name, Password password) throws InvalidUsernameException, InvalidPasswordException;

    /**
     * Get session info by token.
     * @throws InvalidTokenException if token is invalid.
     */
    Session getSession(Token token) throws InvalidTokenException;
}
