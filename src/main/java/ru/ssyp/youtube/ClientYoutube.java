package ru.ssyp.youtube;

import java.io.*;
import java.net.Socket;
import java.io.InputStream;

public class ClientYoutube implements Youtube {
    private final Socket clientSocket = new Socket("0.0.0.0", 8080);

    public ClientYoutube() throws IOException {

    }

    //    @Override
    public void upload(User user, uploadSignature str, InputStream stream) {
        // todo: 1) Делаю запрос на подключение к серверу. Отправляю компанду на сохранение файла
        //       2) Отправляю файл на сервер по частям
        //       3) После того, как отправил файл, жду от сервера контрольную сумму.
        //       4) Получив контрольную сумму, сравниваю её с файлом. Отправляю ОК на сервер
        try {
            String title = str.title;
            String description = str.discreaption;
//            Socket clientSocket = new Socket(localhost, 8080);
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
            byte[] part = stream.readNBytes(1024^2);
            while (part.toString().isEmpty()) {
                dOut.writeUTF(title + description + new String(part) + part.length);
                dOut.flush();
                part = stream.readNBytes(1024^2);
            }
        } catch (java.io.IOException e) {
            String i = "да как так то :(";
            System.out.println(i);
        }
    }
//    @Override
//    public InputStream load(User user, String name) {
        // todo: Очень похож на серверный upload
//        throw new UnsupportedOperationException("Unimplemented method 'load'");
//    }
    public static void main(String[] args) {
        try {
            while (true) {
                String a = System.console().readLine();
                byte[] bcnt = a.getBytes();
                if (bcnt[0] == (byte) 0x00) ;
                {
                    new ClientYoutube().getVideoInfo(a);
                }
                if (bcnt[0] == (byte) 0x01 || bcnt[0] == (byte) 0x02 || bcnt[0] == (byte) 0x03 || bcnt[0] == (byte) 0x04)
                    ;
                {
                    new ClientYoutube().videoList(a);
                }
                if (bcnt[0] == (byte) 0x05) ;
                {
                    new ClientYoutube().uploadVideo(a);
                }
            }
        } catch (IOException e){
            System.out.println("Бывает");
        }
    }
    public void getVideoInfo(String a){
        try {
//            Socket clientSocket = new Socket("0.0.0.0", 8080);
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            byte[] toWrite = new byte[a.length() + 1];
            toWrite[0] = 0x00;
            byte[] stringBytes = a.getBytes();
            for (int i = 1; i < stringBytes.length; i++) {
                toWrite[i] = stringBytes[i];
            }
            clientSocketStream.write(toWrite);
            clientSocketStream.flush();
            InputStream clientSocketInStream = clientSocket.getInputStream();
            System.out.println(clientSocketInStream);
        } catch (IOException e){
            System.out.println("Капец, у тебя ошибка");
        }
    }

    public void videoList(String a){
        try {
//            Socket clientSocket = new Socket("0.0.0.0", 8080);
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(a.getBytes());
            clientSocketStream.flush();
            InputStream clientSocketInStream = clientSocket.getInputStream();
            System.out.println(clientSocketInStream);
        } catch (IOException e){
            System.out.println("Капец, у тебя ошибка");
        }
    }



    public void uploadVideo(String a) {
        try {
            String[] x = a.split(" ");
            User user = new User(x[0]);
            uploadSignature str = new uploadSignature(x[1], x[2], x[3]);
            String title = str.title;
            String description = str.discreaption;
            String name = str.name;
//            String localhost = new String(x[4]);
//            Socket clientSocket = new Socket(localhost, 8080);
            new ClientYoutube().upload(user, new uploadSignature(title, description, name), new ByteArrayInputStream(x[5].getBytes()));
            InputStream clientSocketInStream = clientSocket.getInputStream();
            System.out.println(clientSocketInStream);
            while (clientSocketInStream.readAllBytes()[0] != 0x01 &&
                    clientSocketInStream.readAllBytes()[0] != 0x02) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocketInStream));
                reader.readLine();
                String b = reader.toString();
                System.out.println(b.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Капец, у тебя ошибка");
        }
    }
}