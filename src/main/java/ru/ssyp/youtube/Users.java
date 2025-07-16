package ru.ssyp.youtube;

public interface Users {
    /** 
     * Add new user.
     *
     * @param name Name is unique.
     * @param password
     * @return
     */
    User addUser(String name, String password);

    User login(String name, String password);
}
