package ru.ssyp.youtube.server;

import ru.ssyp.youtube.Youtube;

import java.io.IOException;
import java.io.InputStream;

public class GetVideoInfoCommand implements Command {

    private final int videoId;

    private final Youtube youtube;

    public GetVideoInfoCommand(int videoId, Youtube youtube) {
        this.videoId = videoId;
        this.youtube = youtube;
    }

    @Override
    public InputStream act() {
        try {
            return youtube.videoInfo(this.videoId).rawContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
