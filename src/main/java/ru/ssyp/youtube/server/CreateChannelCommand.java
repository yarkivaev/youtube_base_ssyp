package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.Channels;
import ru.ssyp.youtube.channel.InvalidChannelDescriptionException;
import ru.ssyp.youtube.channel.InvalidChannelNameException;
import ru.ssyp.youtube.sqlite.SqliteSession;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.InvalidTokenException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.users.Users;

import java.io.InputStream;

public class CreateChannelCommand implements Command {

    private final Session session;

    private final String name;

    private final String description;

    private final Channels channels;

    public CreateChannelCommand(Session session, String name, String description, Channels channels) {
        this.session = session;
        this.name = name;
        this.description = description;
        this.channels = channels;
    }

    public CreateChannelCommand(Token token, String name, String description, Channels channels, Users users) throws InvalidTokenException {
        this(users.getSession(token), name, description, channels);
    }


    public CreateChannelCommand(String token, String name, String description, Channels channels, Users users) throws InvalidTokenException {
        this(new Token(token), name, description, channels, users);
    }


    @Override
    public InputStream act(){
        try {
            channels.addNew(session, name, description);
            return InputStream.nullInputStream();
        } catch (InvalidChannelNameException | InvalidChannelDescriptionException e) {
            throw new RuntimeException(e);
        }
    }
}
