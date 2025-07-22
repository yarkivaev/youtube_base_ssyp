package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

public class SqliteVideos implements Videos {
    @Override
    public int addNew(VideoMetadata metadata) {
        return 0;
    }

    @Override
    public Video video(int videoId) {
        return null;
    }

    @Override
    public Video[] allVideos() {
        return new Video[0];
    }
}
