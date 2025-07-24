package ru.ssyp.youtube.server;
import ru.ssyp.youtube.*;
import ru.ssyp.youtube.password.PbkdfPassword;
import ru.ssyp.youtube.sqlite.PreparedDatabase;
import ru.ssyp.youtube.sqlite.SqliteDatabase;
import ru.ssyp.youtube.sqlite.SqliteUsers;
import ru.ssyp.youtube.sqlite.SqliteVideos;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.VideoMetadata;
// import ru.ssyp.youtube.users.MemoryUsers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import static ru.ssyp.youtube.IntCodec.byteToInt;


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
        /*
         * todo Читаем в цикле данные, которые нам шлёт клиент. Клиент отправляет команды.
         * После каждой команды клиент отправляет флаг, о том, что команда отправленна.
         * Когда сервер видит флаг, он начинает парсить команду. Если парсинг успешный - вызывает
         * соответствующий метод интерфейса ServerYoutube
         */
        System.out.println("Wait for");
        Socket socket = serverSocket.accept();
        System.out.println("accepted");
        while (true) {
            new ClientThread(serverSocket.accept(), youtube).start();
        }

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

    private static class ClientThread extends Thread {
        private final Socket sock;
        private final Youtube youtube;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ClientThread(Socket sock, Youtube youtube) throws IOException {
            this.sock = sock;
            this.youtube = youtube;
            inputStream = sock.getInputStream();
            outputStream = sock.getOutputStream();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] shortByteBuffer = new byte[1];
                    byte[] intByteBuffer = new byte[4];

                    inputStream.read(shortByteBuffer);
                    int intCommand = IntCodec.byteToInt_1(shortByteBuffer);
                    Command command;

                    if (intCommand == 0x01) {
                        inputStream.read(intByteBuffer);
                        int videoId = byteToInt(intByteBuffer);

                        inputStream.read(intByteBuffer);
                        int segmentId = byteToInt(intByteBuffer);

                        inputStream.read(shortByteBuffer);
                        int quality = IntCodec.byteToInt_1(shortByteBuffer);

                        command = new GetVideoSegment(videoId, segmentId, quality, youtube);
                        byte[] videoSegment = command.act().readAllBytes();
                        outputStream.write(IntCodec.intToByte(videoSegment.length));
                        outputStream.write(videoSegment);
                    }
                    if (intCommand == 0x02) {
                        command = new ListVideosCommand(youtube);
                        outputStream.write(command.act().readAllBytes());
                    }
                }
            } catch (IOException ignored) { }
        }
    }

//    public static void main(String[] args) throws SQLException, IOException, InvalidTokenException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InterruptedException {
//        System.out.println("Starting the server");
//        ServerSocket serverSocket = new ServerSocket(8080);
//        PreparedDatabase db = new SqliteDatabase(
//            DriverManager.getConnection("jdbc:sqlite::memory:")
//        );
//        Users users = new SqliteUsers(
//            db,
//            new TokenGenRandomB64(20)
//        );
//        VideoSegments segments = new MemoryVideoSegments(new HashMap<>());
//        Youtube youtube = new ServerYoutube(
//            new SegmentatedYoutube(
//                new FileStorage(),
//                Path.of("ffmpeg"),
//                segments,
//                new String[]{"360"}
//            ),
//            new SqliteVideos(db, segments)
//        );
//
//        Token token = users.addUser("test", new PbkdfPassword("123"));
//        Session session = users.getSession(token);
//        youtube.upload(session, new VideoMetadata("test video", "hmm"), new FileInputStream(Paths.get("src", "test", "resources", "sample-15s.mp4").toFile()));
//
//        new Server(
//            serverSocket,
//            youtube,
//            users
//        ).serve();
//    }
}
















//void ruslan_code() {
//    Socket socket = serverSocket.accept();
//    byte[] shortByteBuffer = new byte[1];
//    byte[] intByteBuffer = new byte[4];
//    InputStream inputStream = socket.getInputStream();
//    OutputStream outputStream = socket.getOutputStream();
//    inputStream.read(shortByteBuffer);
//    //int intCommand = IntCodec.byteToInt_1(shortByteBuffer);
//    //Command command = null;
//    Command commandObj = null;
//    byte[] bytes = new byte[1];
//    inputStream.read(bytes);
//    int command = bytes[0];
//    if (command == 0x00) {
//        System.out.println(command);
//        int videoId = read_int(inputStream);
//        System.out.println(videoId);
//        outputStream.write(videoId);
//        System.out.println("______________");
//        InputStream rawVideoInfo = youtube.videoInfo(videoId).rawContent();
//        // rawVideoInfo -> outputStream
//        commandObj = new GetVideoInfoCommand(videoId, youtube);
//        //outputStream.write( rawVideoInfo);
//    }
//    if (command == 0x01) {
//        inputStream.read(intByteBuffer);
//        int videoId = IntCodec.byteToInt(intByteBuffer);
//
//        inputStream.read(intByteBuffer);
//        int segmentId = IntCodec.byteToInt(intByteBuffer);
//        outputStream.write(segmentId);
//
//        inputStream.read(shortByteBuffer);
//        int quality = IntCodec.byteToInt_1(shortByteBuffer);
//
//        commandObj = new GetVideoSegment(videoId, segmentId, quality, youtube);
//        byte[] videoSegment = commandObj.act().readAllBytes();
//        //outputStream.write(IntCodec.intToByte(videoSegment.length));
//        //outputStream.write(videoSegment);
//    }
//    if (command == 0x02) {
//        //command = new ListVideosCommand(youtube);
//        //outputStream.write(command.act().readAllBytes());
//        commandObj = new ListVideosCommand(youtube);
//    }
//    if (command == 0x03) {
//        System.out.println(command);
//        int length = read_int(inputStream);
//        System.out.println(length);
////                outputStream.write(length);
//
//        String username = read_string(inputStream, length);
//        System.out.println(username);
//
//
//        int length_password = read_int(inputStream);
//        System.out.println(length_password);
////                outputStream.write(length_password);
//
//        String password = read_string(inputStream, length_password);
//        System.out.println(password);
//        commandObj = new LoginCommand(username, new PbkdfPassword(password), users);
//        System.out.println("______________");
//    }
//    if (command == 0x04) {
//        int length = read_int(inputStream);
//        System.out.println(length);
////                outputStream.write(length);
//
//        String username = read_string(inputStream, length);
//        System.out.println(username);
//
//
//        int length_password = read_int(inputStream);
//        System.out.println(length_password);
////                outputStream.write(length_password);
//
//        String password = read_string(inputStream, length_password);
//        System.out.println(password);
//        commandObj = new CreateUserCommand(username, new PbkdfPassword(password), users);
//        System.out.println("______________");
//    }
//    if (command == 0x05) {
//        int length = read_int(inputStream);
//        System.out.println(length);
//        outputStream.write(length);
//
//        String token = read_string(inputStream, length);
//        System.out.println(token);
//
//
//        int length_title = read_int(inputStream);
//        System.out.println(length_title);
//        outputStream.write(length_title);
//
//        String title = read_string(inputStream, length_title);
//        System.out.println(title);
//
//
//        int length_description = read_int(inputStream);
//        System.out.println(length_description);
//        outputStream.write(length_description);
//
//        String description = read_string(inputStream, length_description);
//        System.out.println(description);
//
//
//        byte[] length_8B = new byte[8];
//        inputStream.read(length_8B);
//        long length_8 = byteToInt_8(length_8B);
//        System.out.println(length_8);
//        commandObj = new UploadVideoCommand(
//                users.getSession(new Token(token)),
//                new VideoMetadata(title, description),
//                length_8,
//                inputStream,
//                youtube
//        );
//        System.out.println("______________");
//    }
//    commandObj.act();
//    //inputStream.close();
//    //outputStream.close();
//    //socket.close();
//}