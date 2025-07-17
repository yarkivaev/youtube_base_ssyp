package ru.ssyp.youtube;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class ClientYoutube implements Youtube {



    @Override
    public void upload(User user, String title, String discreaption, String name, File file) {
        // todo: 1) Делаю запрос на подключение к серверу. Отправляю компанду на сохранение файла
        //       2) Отправляю файл на сервер по частям
        //       3) После того, как отправил файл, жду от сервера контрольную сумму.
        //       4) Получив контрольную сумму, сравниваю её с файлом. Отправляю ОК на сервер
        File part = file;
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket clientSocket = serverSocket.accept();
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
            dOut.writeUTF(title + discreaption + part.toString() + file.toString().length());
            dOut.flush();
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            Byte answer = dIn.readByte();
            if (answer==byte(0x00)); {
                System.out.println("молодец");
            }
        } catch (java.io.IOException e) {
            String i = "да как так то :(";
        }


        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

    @Override
    public File load(User user, String name) {
        // todo: Очень похож на серверный upload
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (True) {
                Socket clientSocket = serverSocket.accept();
//        byte[] bcnt = new byte[1];
                OutputStream clientSocketStream = clientSocket.getOutputStream();
//        clientSocketStream.read(bcnt);
                String a = System.console().readLine();
                byte[] bcnt = a.getBytes();
                if (bcnt[0] == (byte) 0x00) ;
                {
                    clientSocketStream.write(String(0x00) + a);
                    clientSocketStream.flush();
                    Socket clientSocketIn = serverSocket.accept();
                    InputStream clientSocketInStream = clientSocketIn.getInputStream();
                }
                if (bcnt[0] == (byte) 0x01) ; {
                    clientSocketStream.write(Byte(a));
                    clientSocketStream.flush();
                    Socket clientSocketIn = serverSocket.accept();
                    InputStream clientSocketInStream = clientSocketIn.getInputStream();
                }
                if (bcnt[0] == (byte) 0x05) ; {
                    User user;
                    String title;
                    String discreaption;
                    String name;
                    File file;
                    upload(user, title, discreaption, name, file);
                    Socket clientSocketIn = serverSocket.accept();
                    InputStream clientSocketInStream = clientSocketIn.getInputStream();
                }
                if (bcnt[0] == (byte) 0x02) ; {
                    clientSocketStream.write((byte) 0x02);
                    clientSocketStream.flush();
                    Socket clientSocketIn = serverSocket.accept();
                    InputStream clientSocketInStream = clientSocketIn.getInputStream();
                }
                if (bcnt[0] == (byte) 0x03) ; {
                    clientSocketStream.write(Byte(a));
                    clientSocketStream.flush();
                    Socket clientSocketIn = serverSocket.accept();
                    InputStream clientSocketInStream = clientSocketIn.getInputStream();
                }
                if (bcnt[0] == (byte) 0x04) ; {
                    clientSocketStream.write(Byte(a));
                    clientSocketStream.flush();
                    Socket clientSocketIn = serverSocket.accept();
                    InputStream clientSocketInStream = clientSocketIn.getInputStream();
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Да как так то :(");
        }
    }
    
}
