package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.Channels;
import ru.ssyp.youtube.channel.ForeignChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.InvalidTokenException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.users.Users;

import java.io.InputStream;

public class RemoveChannelCommand implements Command {
    private final Session session;
    private final Integer channelId;
    private final Channels channels;

    public RemoveChannelCommand(Session session, Integer channelId, Channels channels) {
        this.channelId = channelId;
        this.channels = channels;
        this.session = session;
    }

    public RemoveChannelCommand(Token token, Integer channelId, Channels channels, Users users) throws InvalidTokenException {
        this(users.getSession(token), channelId, channels);
    }

    public RemoveChannelCommand(String  token, Integer channelId, Channels channels, Users users) throws InvalidTokenException {
        this(new Token(token), channelId, channels, users);
    }

    @Override
    public InputStream act() {
        try {
            channels.removeChannel(session, channelId);
            return null;
        } catch (InvalidChannelIdException | ForeignChannelIdException e) {
            throw new RuntimeException(e);
        }
    }
}
