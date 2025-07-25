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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import static ru.ssyp.youtube.IntCodec.byteToInt;
import static ru.ssyp.youtube.IntCodec.byteToInt_1;

public class Server {
    public final ServerSocket serverSocket;
    public final Youtube youtube;
    public final Users users;
    private final Channels channels;

    public Server(ServerSocket serverSocket, Youtube youtube, Users users, Channels channels) {
        this.serverSocket = serverSocket;
        this.youtube = youtube;
        this.users = users;
        this.channels = channels;
    }

    public void serve() throws IOException, InvalidTokenException {
        System.out.println("The server has started");
        while (true) {
            new ClientThread(serverSocket.accept(), youtube, users, channels).start();
        }
    }

    private static class ClientThread extends Thread {
        private final Youtube youtube;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        private final Users users;
        private final Channels channels;

        public ClientThread(Socket sock, Youtube youtube, Users users, Channels channels) throws IOException {
            this.youtube = youtube;
            inputStream = sock.getInputStream();
            outputStream = sock.getOutputStream();
            this.users = users;
            this.channels = channels;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    byte[] shortByteBuffer = new byte[1];
                    byte[] intByteBuffer = new byte[4];
                    byte[] longByteBuffer = new byte[8];

                    inputStream.read(shortByteBuffer);
                    int intCommand = byteToInt_1(shortByteBuffer);
                    Command command;

                    if (intCommand == 0x00) {
                        inputStream.read(intByteBuffer);
                        int videoId = byteToInt(intByteBuffer);

                        command = new GetVideoInfoCommand(videoId, youtube);
                        outputStream.write(command.act().readAllBytes());
                    } else if (intCommand == 0x01) {
                        inputStream.read(intByteBuffer);
                        int videoId = byteToInt(intByteBuffer);

                        inputStream.read(intByteBuffer);
                        int segmentId = byteToInt(intByteBuffer);

                        inputStream.read(shortByteBuffer);
                        int quality = byteToInt_1(shortByteBuffer);

                        command = new GetVideoSegment(videoId, segmentId, quality, youtube);
                        byte[] videoSegment = command.act().readAllBytes();
                        outputStream.write(IntCodec.intToByte(videoSegment.length));
                        outputStream.write(videoSegment);
                    } else if (intCommand == 0x02) {
                        command = new ListVideosCommand(youtube);
                        outputStream.write(command.act().readAllBytes());
                    } else if (intCommand == 0x03) {
                        String username = StringCodec.streamToString(inputStream);
                        String password = StringCodec.streamToString(inputStream);
                        command = new LoginCommand(username, new PbkdfPassword(password), users);
                        outputStream.write(new byte[]{0x00});
                        outputStream.write(command.act().readAllBytes());
                    } else if (intCommand == 0x04) {
                        String username = StringCodec.streamToString(inputStream);
                        String password = StringCodec.streamToString(inputStream);
                        command = new CreateUserCommand(username, new PbkdfPassword(password), users);
                        outputStream.write(new byte[]{0x00});
                        outputStream.write(command.act().readAllBytes());
                    } else if (intCommand == 0x05) {
                        String token = StringCodec.streamToString(inputStream);
                        inputStream.read(intByteBuffer);
                        int channelId = byteToInt(intByteBuffer);
                        String title = StringCodec.streamToString(inputStream);
                        String description = StringCodec.streamToString(inputStream);
                        inputStream.read(intByteBuffer);
                        long fileSize = IntCodec.byteToInt_8(longByteBuffer);
                        command = new UploadVideoCommand(
                                users.getSession(new Token(token)),
                                new VideoMetadata(title, description, channelId),
                                fileSize,
                                inputStream,
                                youtube
                        );
                        command.act();
                    } else if (intCommand == 0x06) {
                        String token = StringCodec.streamToString(inputStream);
                        inputStream.read(intByteBuffer);
                        int videoId = byteToInt(intByteBuffer);


                        command = new DeleteVideoCommand(
                                users.getSession(new Token(token)),
                                videoId,
                                youtube
                        );
                        command.act();
                    } else if (intCommand == 0x07) {
                        inputStream.read(intByteBuffer);
                        int channelId = byteToInt(intByteBuffer);
                        command = new GetChannelInfoCommand(channelId, channels);
                        command.act();
                    } else if (intCommand == 0x08) {
                        String token = StringCodec.streamToString(inputStream);
                        String channelname = StringCodec.streamToString(inputStream);
                        String description = StringCodec.streamToString(inputStream);
                        command = new CreateChannelCommand(
                                users.getSession(new Token(token)),
                                channelname,
                                description,
                                channels
                        );
                        command.act();
                    } else if (intCommand == 0x09) {
                        String token = StringCodec.streamToString(inputStream);
                        inputStream.read(intByteBuffer);
                        int Id = byteToInt(intByteBuffer);
                        command = new RemoveChannelCommand(
                                users.getSession(new Token(token)),
                                Id,
                                channels
                        );
                        command.act();
                    } else if (intCommand == 0x0a) {
                        inputStream.read(intByteBuffer);
                        int channelId = byteToInt(intByteBuffer);
                        inputStream.read(intByteBuffer);
                        int startvideo = byteToInt(intByteBuffer);
                        inputStream.read(intByteBuffer);
                        int count = byteToInt(intByteBuffer);
                        command = new GetChannelVideosCommand(channels.channel(channelId), count, startvideo);

                    } else if (intCommand == 0x0b) {
                        inputStream.read(intByteBuffer);
                        String token = StringCodec.streamToString(inputStream);
                        inputStream.read(intByteBuffer);
                        int channelId = byteToInt(intByteBuffer);
                        command = new SubscribeChannelCommand(new Token(token), users, channels.channel(channelId));
                        command.act().readAllBytes();
                    } else if (intCommand == 0x0c) {
                        inputStream.read(intByteBuffer);
                        String token = StringCodec.streamToString(inputStream);
                        inputStream.read(intByteBuffer);
                        int channelId = byteToInt(intByteBuffer);
                        command = new UnsubscribeChannelCommand(new Token(token), users, channels.channel(channelId));
                        command.act().readAllBytes();
                    } else {
                        // защита от подлянок
                        throw new RuntimeException("invalid command received");
                    }
                }
            } catch (IOException | InvalidTokenException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException, IOException, InvalidTokenException, InterruptedException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidChannelDescriptionException, InvalidChannelNameException, InvalidChannelIdException {
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
                        new String[]{"360"},
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
                users,
                channels
        ).serve();
    }
}
