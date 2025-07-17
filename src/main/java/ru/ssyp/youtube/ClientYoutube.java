package ru.ssyp.youtube;

import java.io.File;
import java.io.InputStream;

public class ClientYoutube implements Youtube {

    @Override
    public void upload(User user, String name, InputStream stream) {
        // todo: 1) Делаю запрос на подключение к серверу. Отправляю компанду на сохранение файла
        //       2) Отправляю файл на сервер по частям
        //       3) После того, как отправил файл, жду от сервера контрольную сумму.
        //       4) Получив контрольную сумму, сравниваю её с файлом. Отправляю ОК на сервер
        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

    @Override
    public InputStream load(User user, String name, Double startSec) {
        // todo: Очень похож на серверный upload
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }
    
}
