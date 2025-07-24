package ru.ssyp.youtube.server;
import ru.ssyp.youtube.*;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.PbkdfPassword;
import ru.ssyp.youtube.sqlite.*;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;
// import ru.ssyp.youtube.users.MemoryUsers;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

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
            new ClientThread(serverSocket.accept(), youtube).start();
        }
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

                    if (intCommand == 0x00) {
                        inputStream.read(intByteBuffer);
                        int videoId = IntCodec.byteToInt(intByteBuffer);

                        command = new GetVideoInfoCommand(videoId, youtube);
                        outputStream.write(command.act().readAllBytes());
                    } else if (intCommand == 0x01) {
                        inputStream.read(intByteBuffer);
                        int videoId = IntCodec.byteToInt(intByteBuffer);

                        inputStream.read(intByteBuffer);
                        int segmentId = IntCodec.byteToInt(intByteBuffer);

                        inputStream.read(shortByteBuffer);
                        int quality = IntCodec.byteToInt_1(shortByteBuffer);

                        command = new GetVideoSegment(videoId, segmentId, quality, youtube);
                        byte[] videoSegment = command.act().readAllBytes();
                        outputStream.write(IntCodec.intToByte(videoSegment.length));
                        outputStream.write(videoSegment);
                    } else if (intCommand == 0x02) {
                        command = new ListVideosCommand(youtube);
                        outputStream.write(command.act().readAllBytes());
                    } else {
                        // защита от подлянок
                        throw new RuntimeException("invalid command received");
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws SQLException, IOException, InvalidTokenException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InterruptedException, InvalidChannelIdException, InvalidChannelDescriptionException, InvalidChannelNameException {
        System.out.println("Starting the server");
        ServerSocket serverSocket = new ServerSocket(8080);
        PreparedDatabase db = new SqliteDatabase(
            DriverManager.getConnection("jdbc:sqlite::memory:")
        );
        Users users = new SqliteUsers(
            db,
            new TokenGenRandomB64(20)
        );
        Channels channels = new SqliteChannels(db);
        VideoSegments segments = new MemoryVideoSegments(new HashMap<>());
        Videos videos = new SqliteVideos(db, segments);
        Youtube youtube = new ServerYoutube(
            new SegmentatedYoutube(
                new FileStorage(),
                Path.of("ffmpeg"),
                segments,
                videos
            ),
            videos
        );

        Token token = users.addUser("test", new PbkdfPassword("123"));
        Session session = users.getSession(token);
        Channel channel = channels.addNew(session, "testchannel", "grr..");
        youtube.upload(
                session,
                new VideoMetadata("test video", "hmm", channel.channelInfo().id()),
                new FileInputStream(Paths.get("src", "test", "resources", "sample-15s.mp4").toFile())
        );

        new Server(
            serverSocket,
            youtube,
            users
        ).serve();
    }
}