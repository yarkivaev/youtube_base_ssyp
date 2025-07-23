package ru.ssyp.youtube;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.server.Server;
import ru.ssyp.youtube.sqlite.SqliteDatabase;
import ru.ssyp.youtube.sqlite.SqliteVideos;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

public class ClientYoutubeTest {

    private Users users;

    private ServerSocket serverSocket;

    private Socket clientSocket;

    private InputStream clientInput;

    private OutputStream clientOutput;

    private Videos videos;

    private Youtube youtube;

    @BeforeEach
    void beforeEach() throws IOException, InvalidTokenException, SQLException {
        serverSocket = new ServerSocket(8080);
        users = new MemoryUsers(
                new HashMap<>(),
                new HashMap<>(),
                new TokenGenRandomB64(20),
                new Random()
        );
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new Server(
                                    serverSocket,
                                    new ScreamingYoutube(),
                                    users
                            ).serve();
                        } catch (IOException | InvalidTokenException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        ).start();
        clientSocket = new Socket("127.0.0.1", 8080);
        clientOutput = clientSocket.getOutputStream();
        clientInput = clientSocket.getInputStream();
        videos = new SqliteVideos(
                new SqliteDatabase(DriverManager.getConnection("jdbc:sqlite::memory:")),
                new MemoryVideoSegments(new HashMap<>())
        );
        youtube = new ClientYoutube(clientSocket, videos);
    }

    @AfterEach
    void afterEach() throws IOException {
        clientInput.close();
        clientOutput.close();
        clientSocket.close();
        serverSocket.close();
    }

    @Test
    void testVideos() throws InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException {
        Token token = users.addUser("testName", new DummyPassword("password"));
        videos.addNew(users.getSession(token), new VideoMetadata("VideoTestTitle", "VideoTestDescription"));
        videos.addNew(users.getSession(token), new VideoMetadata("VideoTestTitle", "VideoTestDescription"));
        videos.addNew(users.getSession(token), new VideoMetadata("VideoTestTitle", "VideoTestDescription"));
        System.out.println(youtube.videos().length);
        assert(youtube.videos().length==3);
    }
}
