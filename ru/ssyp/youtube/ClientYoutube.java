package ru.ssyp.youtube;

import java.io.File;

public class ClientYoutube implements Youtube {

    @Override
    public void upload(User user, String name, File file) {
        // todo: 1) Делаю запрос на подключение к серверу. Отправляю компанду на сохранение файла
        //       2) Отправляю файл на сервер по частям
        //       3) После того, как отправил файл, жду от сервера контрольную сумму.
        //       4) Получив контрольную сумму, сравниваю её с файлом. Отправляю ОК на сервер
        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

    @Override
    public File load(User user, String name) {
        // todo: Очень похож на серверный upload
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }
    
}
