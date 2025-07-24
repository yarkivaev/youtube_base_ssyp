package ru.ssyp.youtube.server;

import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.users.InvalidPasswordException;
import ru.ssyp.youtube.users.InvalidUsernameException;
import ru.ssyp.youtube.users.Users;

import java.io.InputStream;

public class LoginCommand implements Command{
    private final String username;
    private final Password password;
    private final Users users;

    public LoginCommand(String username, Password password, Users users) {
        this.username = username;
        this.password = password;
        this.users = users;
    }

    @Override
    public InputStream act() {
        try {
            return users.login(username, password).rawContent();
        } catch (InvalidUsernameException | InvalidPasswordException e) {
            throw new RuntimeException(e);
        }
    }
}
