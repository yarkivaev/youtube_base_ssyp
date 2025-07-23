package ru.ssyp.youtube.video;

import ru.ssyp.youtube.channel.ForeignChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;

public interface Videos {
    Video addNew(Session session, VideoMetadata metadata) throws InvalidChannelIdException;

    Video video(int videoId);

    Video[] videos();

    void deleteVideo(int id, Session session) throws InvalidVideoIdException, ForeignChannelIdException;
}
