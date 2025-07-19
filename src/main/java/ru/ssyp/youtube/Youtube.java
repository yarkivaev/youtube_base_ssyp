package ru.ssyp.youtube;

import ru.ssyp.youtube.auth.UnauthenticatedException;
import ru.ssyp.youtube.users.Session;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Youtube {
    void upload(Session user, String name, InputStream stream) throws IOException, InterruptedException, UnauthenticatedException;

    /*
     * Отправляет поток данных видео, начиная с startSec секунды
     */
    InputStream load(Session user, String name, int startSegment, int resolution);
}