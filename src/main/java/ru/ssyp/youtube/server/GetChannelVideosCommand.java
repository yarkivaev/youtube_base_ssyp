package ru.ssyp.youtube.server;

import ru.ssyp.youtube.channel.Channel;

import java.io.*;

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
    public InputStream act() throws RuntimeException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(channel.videos(startVideo, count));
        oos.flush();
        byte[] objectBytes = bos.toByteArray();

        return new ByteArrayInputStream(objectBytes);
    }
}
