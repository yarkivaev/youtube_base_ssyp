package ru.ssyp.youtube;

import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Quality;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.*;
import java.io.InputStream;
import java.net.Socket;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public class ClientYoutube implements Youtube {


    private final Socket clientSocket;

    private final Videos videos;

    public ClientYoutube(Socket clientSocket, Videos videos) throws IOException {
        this.clientSocket = clientSocket;
        this.videos = videos;
    }

    @Override
    public Video videoInfo(int videoId) {

        try {
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(new byte[]{0x00});
            clientSocketStream.write(IntCodec.intToByte(videoId));

            int id;
            String title;
            String description;
            int segmentAmount;
            short segmentLength;
            int priority;
            String author;

            byte[] intBuffer = new byte[4];
            byte[] shortBuffer = new byte[1];

            InputStream input = clientSocket.getInputStream();
            input.read(intBuffer);
            segmentAmount = IntCodec.byteToInt(intBuffer);
            input.read(shortBuffer);
            segmentLength = IntCodec.byteToInt_1(shortBuffer);
            title = StringCodec.streamToString(input);
            description = StringCodec.streamToString(input);
            author = StringCodec.streamToString(input);
            input.read(intBuffer);
            priority = IntCodec.byteToInt(intBuffer);
            input.read(intBuffer);
            id = IntCodec.byteToInt(intBuffer);


            return new Video(
                    videoId,
                    new VideoMetadata(
                            title,
                            description
                    ),
                    segmentAmount,
                    segmentLength,
                    Quality.fromPriority(priority),
                    author
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Video[] videos() {
        try {
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(new byte[]{0x02});

            int id;
            String title;
            String description;
            int segmentAmount;
            short segmentLength;
            int priority;
            String author;
            short videoCount;
            List<Video> videosList = new ArrayList<>();

            byte[] intBuffer = new byte[4];
            byte[] shortBuffer = new byte[1];
            InputStream input = clientSocket.getInputStream();
            input.read(shortBuffer);
            videoCount = IntCodec.byteToInt_1(shortBuffer);

            for (int i = 0; i<videoCount; i++) {

                input.read(intBuffer);
                segmentAmount = IntCodec.byteToInt(intBuffer);
                input.read(shortBuffer);
                segmentLength = IntCodec.byteToInt_1(shortBuffer);
                title = StringCodec.streamToString(input);
                description = StringCodec.streamToString(input);
                author = StringCodec.streamToString(input);
                input.read(intBuffer);
                priority = IntCodec.byteToInt(intBuffer);
                input.read(intBuffer);
                id = IntCodec.byteToInt(intBuffer);
                input.read(shortBuffer);
                videoCount = IntCodec.byteToInt_1(shortBuffer);
                videosList.add(new Video(
                        id,
                        new VideoMetadata(
                                title,
                                description
                        ),
                        segmentAmount,
                        segmentLength,
                        Quality.fromPriority(priority),
                        author
                ));
            }
            return videosList.toArray(new Video[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void upload(Session user, VideoMetadata str, InputStream stream) throws IOException, InterruptedException {
        try {
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(new byte[]{0x05});
            String title = str.title;
            String description = str.description;
            byte[] part = stream.readNBytes(1024 ^ 2);
            while (part.toString().isEmpty()) {
                clientSocketStream.write(user.toString().getBytes());
                clientSocketStream.write(title.getBytes());
                clientSocketStream.write(description.getBytes());
                clientSocketStream.write((new String(part)).getBytes());
                clientSocketStream.write(part.length);
                clientSocketStream.flush();
                part = stream.readNBytes(1024 ^ 2);
            }
        } catch (java.io.IOException e) {
            String i = "да как так то :(";
            System.out.println(i);
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream load(int videoId, int startSegment, int resolution) {
        try {
            OutputStream clientSocketStream = clientSocket.getOutputStream();
            clientSocketStream.write(new byte[]{0x02});
            clientSocketStream.write(new byte[videoId]);
            clientSocketStream.write(new byte[startSegment]);
            clientSocketStream.write(new byte[resolution]);

            InputStream clientSocketInStream = clientSocket.getInputStream();
            return clientSocketInStream;
        } catch (IOException e) {
            System.out.println("OKAK");
            throw new RuntimeException(e);
        }
    }

}
