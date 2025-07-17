package ru.ssyp.youtube;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.io.*;
import java.net.*;

public class NetworkStorage implements Storage {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
                Socket clientSocket = serverSocket.accept();
//                new ClientHandler(clientSocket).start();
                byte[] bcnt = new byte[1];
                InputStream clientSocketStream = clientSocket.getInputStream();
                clientSocketStream.read(bcnt);
                int cnt;
                if (bcnt[0]==(byte)0x0) {
                    cnt = 0;
                } else {
                    cnt = 1;
                }
                int i = 0;
                String g = "";
                do {
                    clientSocketStream.read(bcnt);
                    if (bcnt[0] == 0x26) {
                        System.out.println("Hello");
                    }
                } while(true);
//                while (clientSocketStream.read()!="&") {
//                    clientSocketStream.read(bcnt);
//                }
//                if (cnt == 0) {
//                    upload(g, clientSocket.decode());
//                } else {
//                    clientSocket.sendto.download(g);
//                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void upload(String name, File file) {

    }

    @Override
    public File download(String name) throws IOException {
        File myObj = new File(name);
        return myObj;
    }
}
