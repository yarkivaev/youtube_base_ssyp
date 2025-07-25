package ru.ssyp.youtube.server;

import ru.ssyp.youtube.Youtube;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.video.InvalidVideoIdException;
import ru.ssyp.youtube.video.Quality;

import java.io.InputStream;
import java.sql.SQLException;

public class GetVideoSegment implements Command {

    private final int videoId;

    private final int segmentId;

    private final Quality quality;

    private final Youtube youtube;

    public GetVideoSegment(int videoId, int segmentId, Quality quality, Youtube youtube) {
        this.videoId = videoId;
        this.segmentId = segmentId;
        this.quality = quality;
        this.youtube = youtube;
    }

    public GetVideoSegment(int videoId, int segmentId, int priority, Youtube youtube) {
        this(videoId, segmentId, Quality.fromPriority(priority), youtube);
    }

    @Override
    public InputStream act() throws InvalidVideoIdException, InvalidChannelIdException {
        return youtube.load(videoId, segmentId, quality.resolution);
    }
}
