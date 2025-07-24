package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.Channel;
import ru.ssyp.youtube.channel.ChannelInfo;
import ru.ssyp.youtube.channel.Channels;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class GetChannelInfoCommand implements Command {
    private final Channel channel;

    public GetChannelInfoCommand(Channel channel) {
        this.channel = channel;
    }

    public GetChannelInfoCommand(int channelId, Channels channels) {
        this(channels.channel(channelId));
    }

    @Override
    public InputStream act() {
        ChannelInfo channelInfo = channel.channelInfo();
        try {
            return channelInfo.rawContent();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
