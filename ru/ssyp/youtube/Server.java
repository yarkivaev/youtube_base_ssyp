package ru.ssyp.youtube;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    
    public final ServerSocket socket;

    public final ServerYoutube serverYoutube;

    public final Users users;

    public Server(ServerSocket socket, ServerYoutube serverYoutube, Users users) {
        this.socket = socket;
        this.serverYoutube = serverYoutube;
        this.users = users;
    }
    public void serve() throws IOException {
        /**
         * todo Читаем в цикле данные, которые нам шлёт клиент. Клиент отправляет команды.
         * После каждой команды клиент отправляет флаг, о том, что команда отправленна.
         * Когда сервер видит флаг, он начинает парсить команду. Если парсинг успешный - вызывает
         * соответствующий метод интерфейса ServerYoutube
         */

        while (true) {
            new ClientThread(socket.accept()).start();
        }
    }

    private class ClientThread extends Thread {
        private final Socket sock;
        private final DataInputStream is;

        public ClientThread(Socket sock) throws IOException {
            this.sock = sock;
            is = new DataInputStream(sock.getInputStream());
        }

        private String readString() throws IOException {
            int length = is.readUnsignedShort();
            return new String(is.readNBytes(length), StandardCharsets.UTF_8);
        }

        @Override
        public void run() {
            try {
                int cmd = is.readUnsignedByte();

                if (cmd == 0x00) {
                    String username = readString();
                    String password = readString();
                    String videoName = readString();

                    User user = users.login(username, password);
                    serverYoutube.upload(user, videoName, null);
                }
            } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) throws IOException {
        new Server(
                new ServerSocket(8080),
                new ServerYoutube(),
                new Users() {
                    @Override
                    public User addUser(String name, String password) {
                        throw new UnsupportedOperationException("addUser");
                    }

                    @Override
                    public User login(String name, String password) {
                        throw new UnsupportedOperationException("login");
                    }
                }
        ).serve();
    }
}