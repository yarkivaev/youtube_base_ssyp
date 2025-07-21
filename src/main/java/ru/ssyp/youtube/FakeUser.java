package ru.ssyp.youtube;

import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.Session;

public class FakeUser implements Session {

    @Override
    public int userId() {
        return 0;
    }

    @Override
    public String username() {
        return "That is null";
    }

    @Override
    public Token token() {
        return new Token("That is token");
    }
}
