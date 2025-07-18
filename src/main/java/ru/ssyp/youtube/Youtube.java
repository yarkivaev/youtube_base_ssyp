package ru.ssyp.youtube;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Youtube {
    void upload(User user, String name, InputStream stream) throws IOException, InterruptedException;

    /*
     * Отправляет поток данных видео, начиная с startSec секунды
     */
    InputStream load(User user, String name, int startSegment, int resolution);
}