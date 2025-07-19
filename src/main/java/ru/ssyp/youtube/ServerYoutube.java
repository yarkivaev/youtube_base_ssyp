package ru.ssyp.youtube;

import ru.ssyp.youtube.users.Session;

import java.io.InputStream;

public class ServerYoutube implements Youtube {

    @Override
    public void upload(Session user, String name, InputStream stream) {
        // todo: Релизовать метод сохранения файла в хранилище.
        //       Сервер должен слушать порт 8080 и принимать входящие подключения
        //       Получив команду на сохранение файла, сервер должен получить файл
        //       Получив файл, сервер отправляет контрольную сумму клиенту
        //       Сервер получает ОК от клиента.
        //       Сервер сохраняет видео в Хранилище.
        //       Сервер отправляет ОК клиенту.
        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

    @Override
    public InputStream load(Session user, String name, int startSegment, int resolution) {
        // TODO Очень похож на клиентский upload.
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }
    
}
