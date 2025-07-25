package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.Channel;

import java.io.InputStream;

public class GetChannelVideosCommand implements Command {
    private final Channel channel;
    private final int count;
    private final int startVideo;

    public GetChannelVideosCommand(Channel channel, int count, int startVideo) {
        this.channel = channel;
        this.count = count;
        this.startVideo = startVideo;
    }

    @Override
    public InputStream act() throws RuntimeException {
        return null;
    }
}
