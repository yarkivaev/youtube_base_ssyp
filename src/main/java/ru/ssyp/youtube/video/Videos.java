package ru.ssyp.youtube.video;

import ru.ssyp.youtube.channel.ForeignChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;

import java.sql.SQLException;

public interface Videos {
    Video addNew(Session session, VideoMetadata metadata) throws InvalidChannelIdException;

    Video video(int videoId) throws InvalidVideoIdException;

    void deleteVideo(int id, Session session) throws InvalidVideoIdException, ForeignChannelIdException;
}
