package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.Channel;
import ru.ssyp.youtube.channel.InvalidUserIdException;
import ru.ssyp.youtube.channel.NotSubscribedException;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.InvalidTokenException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.users.Users;

import java.io.InputStream;

public class UnsubscribeChannelCommand implements Command {
    private final Session session;
    private final Channel channel;

    public UnsubscribeChannelCommand(Session session, Channel channel) {
        this.session = session;
        this.channel = channel;
    }

    public UnsubscribeChannelCommand(Token token, Users users, Channel channel) throws InvalidTokenException {
        this(users.getSession(token), channel);
    }

    public UnsubscribeChannelCommand(String token, Users users, Channel channel) throws InvalidTokenException {
        this(new Token(token), users, channel);
    }

    @Override
    public InputStream act() throws RuntimeException {
        try {
            channel.unsubscribe(session.userId());
            return null;
        } catch (InvalidUserIdException | NotSubscribedException e) {
            throw new RuntimeException(e);
        }
    }
}
