package ru.ssyp.youtube;
import java.net.Socket;
public class Server {
    
    public final Socket socket;

    public final ServerYoutube serverYoutube;

    public Server(Socket socket, ServerYoutube serverYoutube) {
        this.socket = socket;
        this.serverYoutube = serverYoutube;
    }
    public void serve() {
        /**
         * todo Читаем в цикле данные, которые нам шлёт клиент. Клиент отправляет команды.
         * После каждой команды клиент отправляет флаг, о том, что команда отправленна.
         * Когда сервер видит флаг, он начинает парсить команду. Если парсинг успешный - вызывает
         * соответствующий метод интерфейса ServerYoutube
         */
    }
}