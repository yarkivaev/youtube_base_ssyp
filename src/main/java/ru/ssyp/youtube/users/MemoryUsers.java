package ru.ssyp.youtube.users;

import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGen;

import java.util.Map;
import java.util.Random;

public class MemoryUsers implements Users {

    class UserIdentiter {
        public final String username;

        public final int userId;


        UserIdentiter(String username, int userId) {
            this.username = username;
            this.userId = userId;
        }
    }

    private final Map<String, Password> users;



    private final Map<Token, UserIdentiter> sessions;

    private final TokenGen tokenGen;

    private final Random random;

    public MemoryUsers(
            Map<String, Password> users,
            Map<Token, UserIdentiter> sessions,
            TokenGen tokenGen,
            Random random
    ) {
        this.users = users;
        this.sessions = sessions;
        this.tokenGen = tokenGen;
        this.random = random;
    }

    @Override
    public Token addUser(String name, Password password) throws InvalidUsernameException, InvalidPasswordException, UsernameTakenException {
        users.put(name, password);
        return this.login(name, password);
    }

    @Override
    public Token login(String name, Password password) throws InvalidUsernameException, InvalidPasswordException {
        if (users.keySet().contains(name)) {
            if (users.get(name).equals(password)) {
                Token token =  tokenGen.token();
                sessions.put(token, new UserIdentiter(name, random.nextInt()));
                return token;
            } else {
                throw new InvalidPasswordException();
            }
        } else {
            throw new InvalidUsernameException();
        }
    }

    @Override
    public Session getSession(Token token) throws InvalidTokenException {
        if (sessions.keySet().contains(token)) {
            UserIdentiter userIdentiter = sessions.get(token);
            return new Session() {
                @Override
                public int userId() {
                    return userIdentiter.userId;
                }

                @Override
                public String username() {
                    return userIdentiter.username;
                }

                @Override
                public Token token() {
                    return token;
                }
            };
        } else {
            throw new InvalidTokenException();
        }
    }
}
