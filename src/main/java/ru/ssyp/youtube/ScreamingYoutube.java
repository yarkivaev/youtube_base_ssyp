package ru.ssyp.youtube;

import org.apache.commons.io.input.NullInputStream;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;

import java.io.IOException;
import java.io.InputStream;

public class ScreamingYoutube implements Youtube {
    @Override
    public Video videoInfo(int videoId) {
        System.out.println("VideoInfo has been called: {videoId=" + videoId + "}");
        return Video.fakeVideo();
    }

    @Override
    public Video[] videos() {
        System.out.println("Videos has been called");
        return new Video[0];
    }

    @Override
    public Video upload(Session user, VideoMetadata metadata, InputStream stream) throws IOException, InterruptedException {
        System.out.println("Upload has been called: {session=" + user + ",metadata=" + metadata + "}");
        return null;
    }

    @Override
    public InputStream load(int videoId, int startSegment, int resolution) {
        System.out.println("Load has been called: {videoId=" + videoId + ",startSegment=" + startSegment + ",resolution=" + resolution + "}");
        return new NullInputStream();
    }
}
