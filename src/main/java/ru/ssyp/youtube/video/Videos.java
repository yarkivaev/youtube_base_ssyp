package ru.ssyp.youtube.video;

import ru.ssyp.youtube.users.Session;

public interface Videos {
    int addNew(Session session, VideoMetadata metadata);

    Video video(int videoId);


    Video[] allVideos();
    void deleteVideo(int id);

}
