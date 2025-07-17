package ru.ssyp.youtube;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
    
    public final Socket socket;

    public final ServerYoutube serverYoutube;

    public Server(Socket socket, ServerYoutube serverYoutube) {
        this.socket = socket;
        this.serverYoutube = serverYoutube;
    }
    public void serve() {
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
            String name = "";
            String username = "";
            do {
                clientSocketStream.read(bcnt);
            } while(true);
//            if (cnt == 0) {
//                serverYoutube.upload(new User(username), "title", "decription", name, clientSocketStream);
//            } else {
//                serverYoutube.load(new User(username), name);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}