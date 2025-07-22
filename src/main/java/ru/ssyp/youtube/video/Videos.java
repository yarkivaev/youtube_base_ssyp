package ru.ssyp.youtube.video;

public interface Videos {
    int addNew(VideoMetadata metadata);

    Video video(int videoId);

    Video[] allVideos();
}
