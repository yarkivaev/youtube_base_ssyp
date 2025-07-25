package ru.ssyp.youtube;

import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Youtube {

    Video videoInfo(int videoId);

    Video[] videos();

    Video upload(Session user, VideoMetadata metadata, InputStream stream) throws IOException, InterruptedException, InvalidChannelIdException;

    /*
     * Отправляет поток данных видео, начиная с startSec секунды
     */
    InputStream load(int videoId, int startSegment, int resolution);
}