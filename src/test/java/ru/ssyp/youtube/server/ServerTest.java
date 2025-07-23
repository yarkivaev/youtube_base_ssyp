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
    void listVideosTest() throws IOException, InterruptedException {
        
        clientOutput.write(new byte[]{0x02});
        System.out.println(new BufferedReader(new InputStreamReader(clientInput)).read());

    }
}



