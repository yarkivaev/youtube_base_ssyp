package ru.ssyp.youtube;

import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.io.IOException;
import java.io.InputStream;

// TODO (maybe):
//     move videoInfo and videos from this class into SegmentatedYoutube
//     and remove this
public class ServerYoutube implements Youtube {
    private final Youtube youtube;
    private final Videos videos;

    public ServerYoutube(Youtube youtube, Videos videos) {
        this.youtube = youtube;
        this.videos = videos;
    }

    @Override
    public Video videoInfo(int videoId) {
        return videos.video(videoId);
    }

    @Override
    public Video[] videos() {
        return videos.videos();
    }

    @Override
    public Video upload(Session user, VideoMetadata metadata, InputStream stream) throws IOException, InterruptedException, InvalidChannelIdException {
        return youtube.upload(user, metadata, stream);
    }

    @Override
    public void remove(int videoId, Session session) {

    }

    @Override
    public InputStream load(int videoId, int startSegment, int resolution) {
        return youtube.load(videoId, startSegment, resolution);
    }
}
