package ru.ssyp.youtube.server;
import ru.ssyp.youtube.ServerYoutube;
import ru.ssyp.youtube.Youtube;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    
    public final ServerSocket serverSocket;

    public final Youtube youtube;

    public Server(ServerSocket serverSocket, Youtube youtube) {
        this.serverSocket = serverSocket;
        this.youtube = youtube;
    }
    public void serve() throws IOException {
        /*
         * todo Читаем в цикле данные, которые нам шлёт клиент. Клиент отправляет команды.
         * После каждой команды клиент отправляет флаг, о том, что команда отправленна.
         * Когда сервер видит флаг, он начинает парсить команду. Если парсинг успешный - вызывает
         * соответствующий метод интерфейса ServerYoutube
         */
        Socket socket = serverSocket.accept();


        while (true) {
            Command commandObj;
            byte[] bytes = new byte[1];
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            inputStream.read(bytes);
            int command = bytes[0];
            if (command == 0x00) {
                System.out.println(command);
                int videoId = read_int(inputStream);
                System.out.println(videoId);
                outputStream.write(videoId);
                System.out.println("______________");
                InputStream rawVideoInfo = youtube.videoInfo(videoId).rawContent();
                // rawVideoInfo -> outputStream
                command = new GetVideoInfoCommand();
            }
            if (command == 0x01) {
                System.out.println(command);
                int videoId = read_int(inputStream);
                System.out.println(videoId);
                outputStream.write(videoId);


                int segmentId = read_int(inputStream);
                System.out.println(segmentId);
                outputStream.write(segmentId);

                int quality = inputStream.read();
                System.out.println(quality);
                System.out.println(quality);
                outputStream.write(quality);
                System.out.println("______________");
            }
            if (command == 0x02) {
                System.out.println("ok");
                System.out.println("______________");
                outputStream.write('0');
            }
            if (command == 0x03) {
                System.out.println(command);
                int length = read_int(inputStream);
                System.out.println(length);
                outputStream.write(length);

                String username = read_string(inputStream, length);
                System.out.println(username);


                int length_password = read_int(inputStream);
                System.out.println(length_password);
                outputStream.write(length_password);

                String password = read_string(inputStream , length_password);
                System.out.println(password);
                System.out.println("______________");
            }
            if (command == 0x04) {
                int length = read_int(inputStream);
                System.out.println(length);
                outputStream.write(length);

                String username = read_string(inputStream, length);
                System.out.println(username);


                int length_password = read_int(inputStream);
                System.out.println(length_password);
                outputStream.write(length_password);

                String password = read_string(inputStream, length_password);
                System.out.println(password);
                System.out.println("______________");
            }
            if (command == 0x05) {
                int length = read_int(inputStream);
                System.out.println(length);
                outputStream.write(length);

                String token = read_string(inputStream, length);
                System.out.println(token);


                int length_title = read_int(inputStream);
                System.out.println(length_title);
                outputStream.write(length_title);

                String title = read_string(inputStream , length_title);
                System.out.println(title);


                int length_description = read_int(inputStream);
                System.out.println(length_description);
                outputStream.write(length_description);

                String description = read_string(inputStream , length_description);
                System.out.println(description);


                byte[] length_8B = new byte[8];
                inputStream.read(length_8B);
                long length_8 = byteToInt_8(length_8B);
                System.out.println(length_8);
                System.out.println("______________");
           }
            commandObj.act();
            // -> возврат результата клиенту
        }

    }

    private int byteToInt(byte[] bytes) {

        return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
    }
    private long byteToInt_8(byte[] bytes) {

        return ((long) bytes[0] << 56) + ((long) bytes[1] << 48) + ((long) bytes[2] << 40) + ((long) bytes[3] << 32) + (bytes[4] << 24) + (bytes[5] << 16) + (bytes[6] << 8) + bytes[7];
    }
    private int byteToInt_1(byte[] bytes) {

        return bytes[0];
    }
    private int read_int(InputStream inputStream) throws IOException {
        byte[] lengthB = new byte[4];
        inputStream.read(lengthB);
        System.out.println(lengthB[0] + " " + lengthB[1] + " " + lengthB[2] + " " + lengthB[3]);
        return byteToInt(lengthB);
    }
    private String read_string(InputStream inputStream, int length) throws IOException {
        byte[] stringB = new byte[length];
        inputStream.read(stringB);
        return new String(stringB, StandardCharsets.UTF_8);
    }



    public byte[] subarray(byte[] array, int start, int length) {
        if (start + length > array.length) {
            throw new RuntimeException("subarray exceeds array length");
        }
        byte[] newArray = new byte[length];
        for (int i = start; i < start + length; i++) {
            newArray[start - i] = array[i];
        }
        return newArray;
    }
     //0 1 0 0 start = 1 length = 4

     public static void main(String[] args) throws IOException {
        new Server(
                new ServerSocket(8080),
                new ServerYoutube()
        ).serve();
    }
}