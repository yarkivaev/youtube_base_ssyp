package ru.ssyp.youtube;

import java.io.File;
import java.net.Socket;
import java.io.InputStream;

public class ServerYoutube implements Youtube {

    private final Socket socket;

    public ServerYoutube(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void upload(User user, UploadSignature str, InputStream stream) {
        // todo: Релизовать метод сохранения файла в хранилище.
        //       Сервер должен слушать порт 8080 и принимать входящие подключения
        //       Получив команду на сохранение файла, сервер должен получить файл
        //       Получив файл, сервер отправляет контрольную сумму клиенту
        //       Сервер получает ОК от клиента.
        //       Сервер сохраняет видео в Хранилище.
        //       Сервер отправляет ОК клиенту.
        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

//    @Override
    public InputStream load(User user, String name) {
        // TODO Очень похож на клиентский upload.
        throw new UnsupportedOperationException("Unimplemented method 'load'");
}
}
