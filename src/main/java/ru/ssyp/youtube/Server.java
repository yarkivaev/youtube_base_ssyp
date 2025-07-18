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
            byte[] bcnt = new byte[1];
            InputStream clientSocketStream = clientSocket.getInputStream();
            clientSocketStream.read(bcnt);
            if (bcnt[0] == (byte) 0x00) {

            }
        } catch(IOException e){
                System.out.println("Anything");
        }
        }
    }