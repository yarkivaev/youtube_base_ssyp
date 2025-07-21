package ru.ssyp.youtube.server;

import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.users.InvalidPasswordException;
import ru.ssyp.youtube.users.InvalidUsernameException;
import ru.ssyp.youtube.users.UsernameTakenException;
import ru.ssyp.youtube.users.Users;

import java.io.InputStream;

public class CreateUserCommand implements Command{

    private final String username;

    private final Password password;

    private final Users users;

    public CreateUserCommand(String username, Password password, Users users) {
        this.username = username;
        this.password = password;
        this.users = users;
    }


    @Override
    public InputStream act() {
        try {
            return users.addUser(username, password).rawContent();
        } catch (InvalidUsernameException | InvalidPasswordException | UsernameTakenException e) {
            throw new RuntimeException(e);
        }
    }
}
