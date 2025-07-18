package ru.ssyp.youtube;

import javax.security.auth.kerberos.KerberosKey;
import java.io.*;
import java.net.Socket;
import java.io.InputStream;
import java.util.Optional;

public class ClientYoutube implements Youtube {

    private boolean add;

    @Override
    public void upload(User user, String title, String description, String name, InputStream stream) {
        // todo: 1) Делаю запрос на подключение к серверу. Отправляю компанду на сохранение файла
        //       2) Отправляю файл на сервер по частям
        //       3) После того, как отправил файл, жду от сервера контрольную сумму.
        //       4) Получив контрольную сумму, сравниваю её с файлом. Отправляю ОК на сервер
        try {
            Socket clientSocket = new Socket("0.0.0.0", 8080);
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
            byte[] content = stream.readAllBytes();
            dOut.writeUTF(title + description + new String(content) + content.length);
            dOut.flush();
        } catch (java.io.IOException e) {
            String i = "да как так то :(";
            System.out.println(i);
        }
    }
    @Override
    public InputStream load(User user, String name) {
        // todo: Очень похож на серверный upload
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }
    public static void main(String[] args) {
        while (true) {
            String a = System.console().readLine();
            byte[] bcnt = a.getBytes();
            if (bcnt[0] == (byte) 0x00) ;
            {
                new ClientYoutube().getVideoInfo(a);
            }
            if (bcnt[0] == (byte) 0x01 || bcnt[0] == (byte) 0x02 || bcnt[0] == (byte) 0x03 || bcnt[0] == (byte) 0x04);
            {
                new ClientYoutube().videoList(a);
            }
            if (bcnt[0] == (byte) 0x05) ;
            {
                new ClientYoutube().uploadVideo(a);
            }
        }
    }
    public void getVideoInfo(String a){
        try {
            Socket clientSocket = new Socket("0.0.0.0", 8080);
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
            Socket clientSocket = new Socket("0.0.0.0", 8080);
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
            Socket clientSocket = new Socket("0.0.0.0", 8080);
            String[] x = a.split(" ");
            User user = new User(x[0]);
            String title = new String(x[1]);
            String discreaption = new String(x[2]);
            String name = new String(x[3]);
            new ClientYoutube().upload(user, title, discreaption, name, new ByteArrayInputStream(x[4].getBytes()));
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
