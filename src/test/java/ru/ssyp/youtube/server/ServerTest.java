package ru.ssyp.youtube.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.ssyp.youtube.ScreamingYoutube;
import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.password.PbkdfPassword;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.VideoMetadata;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

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
    }

    @AfterEach
    void afterEach() throws IOException {
        System.out.println("HELLLo");
        clientInput.close();
        clientOutput.close();
        clientSocket.close();
        serverSocket.close();
    }

    @Test
    void getVideoInfoTest() throws IOException, InterruptedException {
        byte[] command = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00};
        clientOutput.write(command);
        Thread.sleep(500);
        System.out.println(new BufferedReader(new InputStreamReader(clientInput)).read());
    }



    @Test
    void getVideoSegmentTest() throws IOException, InterruptedException {
        int videoId = 42;
        int segmentId = 5;
        short quality = 3;
        byte[] command = new byte[] {
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
    void ListVideosTest() throws IOException, InterruptedException {

    }

    @Test
    void loginTest() throws IOException, InterruptedException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException {
        String username = "Testuser777";
        Password password = new PbkdfPassword("a56.6.912ddv");

        users.addUser(username , password);

        byte[] command = new byte[] {
                0x03,
                0x00, 0x00, 0x00, 0x0b,
                0x54, 0x65, 0x73, 0x74, 0x75, 0x73, 0x65, 0x72, 0x37, 0x37, 0x37,
                0x00, 0x00, 0x00, 0x0c,
                0x61, 0x35, 0x36, 0x2e, 0x36, 0x2e, 0x39, 0x31, 0x32, 0x64, 0x64, 0x76
        };
        clientOutput.write(command);
        Thread.sleep(500);
//        System.out.println(new InputStreamReader(clientInput));
    }

    @Test
    void CreateUserTest() throws IOException, InterruptedException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException {
        String username = "Testuser777";
        Password password = new PbkdfPassword("a56.6.912ddv");



        byte[] command = new byte[] {
                0x04,
                0x00, 0x00, 0x00, 0x0b,
                0x54, 0x65, 0x73, 0x74, 0x75, 0x73, 0x65, 0x72, 0x37, 0x37, 0x37,
                0x00, 0x00, 0x00, 0x0c,
                0x61, 0x35, 0x36, 0x2e, 0x36, 0x2e, 0x39, 0x31, 0x32, 0x64, 0x64, 0x76
        };
        clientOutput.write(command);
        Thread.sleep(500);
    }

    @Test
    void UploadVideoTest() throws IOException, InterruptedException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException {
        Token token = new Token("1111184543");
        VideoMetadata metadata = new VideoMetadata("duckroll","prokatitsa -- 5000rubley");
        long filesize = 56;


        byte[] command = new byte[] {
                0x05,
                0x00, 0x00, 0x00, 0x08,
                0x39, 0x36, 0x33, 0x36, 0x31, 0x32, 0x33, 0x34,
                0x00, 0x00, 0x00, 0x08,
                0x64, 0x75, 0x63, 0x6b, 0x72, 0x6f, 0x6c, 0x6c,
                0x00, 0x00, 0x00, 0x18,
                0x70, 0x72, 0x6f, 0x6b, 0x61, 0x74, 0x69, 0x74, 0x73, 0x61, 0x20, 0x2d, 0x2d, 0x20, 0x35, 0x30, 0x30, 0x30, 0x72, 0x75, 0x62, 0x6c, 0x65, 0x79,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08
        };
        clientOutput.write(command);
        Thread.sleep(500);
    }
}



