package ru.ssyp.youtube.server;

import ru.ssyp.youtube.IntCodec;
import ru.ssyp.youtube.channel.Channel;
import ru.ssyp.youtube.channel.ChannelInfo;
import ru.ssyp.youtube.channel.Channels;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.InvalidTokenException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.users.Users;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public class GetUserChannelsCommand implements Command {
    private final Users users;
    private final Channels channels;
    private final Token token;

    public GetUserChannelsCommand(Users users, Channels channels, Token token) {
        this.users = users;
        this.channels = channels;
        this.token = token;
    }

    @Override
    public InputStream act() throws RuntimeException {
        try {
            Session session = users.getSession(token);
            Channel[] cls = channels.getUserChannel(session.userId());

            return new SequenceInputStream(
                    new ByteArrayInputStream(IntCodec.intToByte(cls.length)),
                    new SequenceInputStream(
                            Collections.enumeration(
                                    Arrays.stream(cls).map(c -> {
                                        ChannelInfo info = c.channelInfo();

                                        try {
                                            return new SequenceInputStream(
                                                    new ByteArrayInputStream(IntCodec.intToByte(info.id())),
                                                    info.rawContent()
                                            );
                                        } catch (IOException | SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }).toList()
                            )
                    )
            );
        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }
    }
}
