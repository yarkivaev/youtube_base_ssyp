package ru.ssyp.youtube.server;

import ru.ssyp.youtube.IntCodec;
import ru.ssyp.youtube.Youtube;
import ru.ssyp.youtube.video.Video;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;

public class ListVideosCommand implements Command {
    private final Youtube youtube;

    public ListVideosCommand(Youtube youtube) {
        this.youtube = youtube;
    }

    @Override
    public InputStream act() {
        Video[] videos = youtube.videos();
        return new SequenceInputStream(
                new ByteArrayInputStream(IntCodec.intToByte(videos.length)),
                new SequenceInputStream(
                        Collections.enumeration(
                                Arrays.stream(videos)
                                        .map(v -> {
                                            try {
                                                return v.rawContent();
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        })
                                        .toList()
                        )
                )
        );
    }
}
