package ru.ssyp.youtube.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.IntCodec;
import ru.ssyp.youtube.ScreamingYoutube;
import ru.ssyp.youtube.StringCodec;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.password.PbkdfPassword;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.InvalidTokenException;
import ru.ssyp.youtube.users.MemoryUsers;
import ru.ssyp.youtube.users.Users;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    private Users users;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private InputStream clientInput;
    private OutputStream clientOutput;

    @BeforeEach
    void beforeEach() throws IOException, InvalidTokenException {
        serverSocket = new ServerSocket(8080);
        users = new MemoryUsers(
                new HashMap<>(),
                new HashMap<>(),
                new TokenGenRandomB64(20),
                new Random()
        );

        new Thread(
                () -> {
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
        ).start();

        clientSocket = new Socket("127.0.0.1", 8080);
        clientOutput = clientSocket.getOutputStream();
        clientInput = clientSocket.getInputStream();
    }

    @AfterEach
    void afterEach() throws IOException {
        clientInput.close();
        clientOutput.close();
        clientSocket.close();
        serverSocket.close();
    }

    @Test
    void getVideoInfoTest() throws Exception {
        byte[] command = {0x00, 0x00, 0x00, 0x00, 0x00};
        clientOutput.write(command);
        Thread.sleep(500);
        byte[] videoInfo = new byte[1];
        clientInput.read(videoInfo);
        assertEquals(0, videoInfo[0]);
    }

    @Test
    void getVideoSegmentTest() throws Exception {
        byte[] command = {
                0x01,
                0x00, 0x00, 0x00, 0x42,
                0x00, 0x00, 0x00, 0x05,
                0x03
        };
        clientOutput.write(command);
        Thread.sleep(500);
        System.out.println(new BufferedReader(new InputStreamReader(clientInput)).read());
    }

    @Test
    void listVideosTest() throws Exception {
        clientOutput.write(new byte[]{0x02});
        byte[] bytes = new byte[1];
        clientInput.read(bytes);
        assertEquals(0, bytes[0]);
    }

    @Test
    void loginTest() throws Exception {
        String username = "Testuser777";
        Password password = new PbkdfPassword("a56.6.912ddv");

        users.addUser(username, password);

        byte[] command = {
                0x03,
                0x00, 0x00, 0x00, 0x0b,
                0x54, 0x65, 0x73, 0x74, 0x75, 0x73, 0x65, 0x72, 0x37, 0x37, 0x37,
                0x00, 0x00, 0x00, 0x0c,
                0x61, 0x35, 0x36, 0x2e, 0x36, 0x2e, 0x39, 0x31, 0x32, 0x64, 0x64, 0x76
        };
        clientOutput.write(command);
        Thread.sleep(500);
    }

    @Test
    void createUserTest() throws Exception {
        String username = "Testuser777";
        Password password = new PbkdfPassword("a56.6.912ddv");
        clientOutput.write(new byte[]{0x04});
        clientOutput.write(StringCodec.stringToStream(username));
        clientOutput.write(StringCodec.stringToStream(password.value()));
        byte[] bytes = new byte[1];
        clientInput.read(bytes);
        assertEquals(0, bytes[0]);
    }

    @Test
    void UploadVideoTest() throws Exception {
        Token token = users.addUser("testUser", new DummyPassword("testPass"));
        int channelId = 42;
        String title = "testVideo";
        String description = "Desc";
        int fileSize = 3;
        InputStream file = new ByteArrayInputStream(new byte[]{0x00, 0x00, 0x00});
        clientOutput.write(token.rawContent().readAllBytes());
        clientOutput.write(IntCodec.intToByte(channelId));
        clientOutput.write(StringCodec.stringToStream(title));
        clientOutput.write(StringCodec.stringToStream(description));
        clientOutput.write(IntCodec.intToByte(fileSize));
        clientOutput.write(file.readAllBytes());
        byte[] bytes = new byte[1];
        clientInput.read(bytes);
        assertEquals(0, bytes[0]);
    }
}
