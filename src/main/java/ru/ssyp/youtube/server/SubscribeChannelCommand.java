package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.AlreadySubscribedException;
import ru.ssyp.youtube.channel.Channel;
import ru.ssyp.youtube.channel.InvalidUserIdException;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.InvalidTokenException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.users.Users;

import java.io.InputStream;
import java.sql.SQLException;

public class SubscribeChannelCommand implements Command {

    private final Session session;

    private final Channel channel;


    public SubscribeChannelCommand(Session session, Channel channel) {
        this.session = session;
        this.channel = channel;
    }

    public SubscribeChannelCommand(Token token, Users users, Channel channel) throws InvalidTokenException {
        this(users.getSession(token), channel);
    }

    public SubscribeChannelCommand(String token, Users users, Channel channel) throws InvalidTokenException {
        this(new Token(token), users, channel);
    }

    @Override
    public InputStream act() {
        try {
            channel.subscribe(session.userId());
            return null;
        } catch (InvalidUserIdException | AlreadySubscribedException e) {
            throw new RuntimeException(e);
        }
    }
}

