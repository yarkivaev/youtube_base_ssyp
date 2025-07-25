package ru.ssyp.youtube;

import org.apache.commons.lang3.NotImplementedException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;

import java.io.IOException;
import java.io.InputStream;

public class ClientYoutube implements Youtube {

    @Override
    public Video videoInfo(int videoId) {
        return null;
    }

    @Override
    public Video[] videos() {
        return new Video[0];
    }

    @Override
    public Video upload(Session user, VideoMetadata metadata, InputStream stream) throws IOException, InterruptedException {
        throw new NotImplementedException();
    }

    @Override
    public void remove(int videoId, Session session) {

    }

    @Override
    public InputStream load(int videoId, int startSegment, int resolution) {
        return null;
    }


}
