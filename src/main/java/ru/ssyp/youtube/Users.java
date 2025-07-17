package ru.ssyp.youtube;

public interface Users {
    /** 
     * Register new user.
     *
     * @param name Name is unique.
     * @return token
     */
    String addUser(String name, String password);

    /**
     * Log in as existing user.
     *
     * @param name Name is unique.
     * @return token
     */
    String login(String name, String password);

    /**
     * Get user info by token.
     * @return User or null if token is invalid.
     */
    User getUser(String token);
}
