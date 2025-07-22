package ru.ssyp.youtube.server;

import ru.ssyp.youtube.Youtube;
import ru.ssyp.youtube.video.Quality;

import java.io.InputStream;

public class ListVideosCommand implements Command{

    private final Youtube youtube;

    public ListVideosCommand(Youtube youtube) {
        this.youtube = youtube;
    }

    @Override
    public InputStream act() {
//        return youtube.videos().;
        return null;
    }
}
