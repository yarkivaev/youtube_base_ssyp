package ru.ssyp.youtube.server;
import ru.ssyp.youtube.IntCodec;
import ru.ssyp.youtube.ScreamingYoutube;
import ru.ssyp.youtube.Youtube;
import ru.ssyp.youtube.sqlite.SqliteDatabase;
import ru.ssyp.youtube.sqlite.SqliteUsers;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.InvalidTokenException;
// import ru.ssyp.youtube.users.MemoryUsers;
import ru.ssyp.youtube.users.Users;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {
    
    public final ServerSocket serverSocket;

    public final Youtube youtube;

    public final Users users;

    public Server(ServerSocket serverSocket, Youtube youtube, Users users) {
        this.serverSocket = serverSocket;
        this.youtube = youtube;
        this.users = users;
    }
    public void serve() throws IOException, InvalidTokenException {
        while (true) {
            Socket socket = serverSocket.accept();
            byte[] shortByteBuffer = new byte[1];
            byte[] intByteBuffer = new byte[4];
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            inputStream.read(shortByteBuffer);
            int intCommand = IntCodec.byteToInt_1(shortByteBuffer);
            Command command = null;
            if (intCommand == 0x01) {
                inputStream.read(intByteBuffer);
                int videoId = IntCodec.byteToInt(intByteBuffer);

                inputStream.read(intByteBuffer);
                int segmentId = IntCodec.byteToInt(intByteBuffer);
                outputStream.write(segmentId);

                inputStream.read(shortByteBuffer);
                int quality = IntCodec.byteToInt_1(shortByteBuffer);

                command = new GetVideoSegment(videoId , segmentId, quality, youtube);
                byte[] videoSegment = command.act().readAllBytes();
                outputStream.write(IntCodec.intToByte(videoSegment.length));
                outputStream.write(videoSegment);
            }
            if (intCommand == 0x02) {
                command = new ListVideosCommand(youtube);
                outputStream.write(command.act().readAllBytes());
            }
        }
    }

    public static void main(String[] args) throws SQLException, IOException, InvalidTokenException {
        System.out.println("Starting the server");
        ServerSocket serverSocket = new ServerSocket(8080);
        Users users = new SqliteUsers(
            new SqliteDatabase(
                DriverManager.getConnection("jdbc:sqlite::memory:")
            ),
            new TokenGenRandomB64(20)
        );
        new Server(
            serverSocket,
            new ScreamingYoutube(),
            users
        ).serve();
    }
}