package ru.ssyp.youtube;

import ru.ssyp.youtube.channel.ForeignChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.InvalidVideoIdException;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;

import java.io.IOException;
import java.io.InputStream;

public interface Youtube {
    Video videoInfo(int videoId);

    Video[] videos();

    Video upload(Session user, VideoMetadata metadata, InputStream stream) throws IOException, InterruptedException, InvalidChannelIdException;

    void remove(int videoId, Session session) throws InvalidVideoIdException, IOException, ForeignChannelIdException;

    /*
     * Отправляет поток данных видео, начиная с startSec секунды
     */
    InputStream load(int videoId, int startSegment, int resolution) throws InvalidVideoIdException, InvalidChannelIdException;
}
