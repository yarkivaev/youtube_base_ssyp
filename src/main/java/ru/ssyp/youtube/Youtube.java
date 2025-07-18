package ru.ssyp.youtube;

import java.io.InputStream;

public interface Youtube {
    void upload(Session user, String name, InputStream stream);

    /*
     * Отправляет поток данных видео, начиная с startSec секунды
     */
    InputStream load(Session user, String name, Double startSec);
}