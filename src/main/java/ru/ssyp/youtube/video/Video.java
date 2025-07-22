package ru.ssyp.youtube.video;

import ru.ssyp.youtube.ProtocolValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Video implements ProtocolValue {
    public final int id;

    public final VideoMetadata metadata;

    public final int segmentAmount;

    public final short segmentLength;

    public final Quality maxQuality;

    public final String author;

    public Video(
            int id,
            VideoMetadata metadata,
            int segmentAmount,
            short segmentLength,
            Quality maxQuality,
            String author
    ) {
        this.id = id;
        this.metadata = metadata;
        this.segmentAmount = segmentAmount;
        this.segmentLength = segmentLength;
        this.maxQuality = maxQuality;
        this.author = author;
    }

    @Override
    public InputStream rawContent() throws IOException {
//        byte[] segmentAmount = ...;
//        byte[] segmentLength = ...;
//        byte[] maxQuality = this.maxQuality.rawContent().readAllBytes();
//        byte[] authorName = ...;


//        try {
//            OutputStream clientSocketStream = clientSocket.getOutputStream();
//            byte[] toWrite = new byte[a.length() + 1];
//            toWrite[0] = 0x00;
//            byte[] stringBytes = a.getBytes();
//            for (int i = 1; i < stringBytes.length; i++) {
//                toWrite[i] = stringBytes[i];
//            }
//            clientSocketStream.write(toWrite);
//            clientSocketStream.flush();
//            InputStream clientSocketInStream = clientSocket.getInputStream();
//            System.out.println(clientSocketInStream);
//        } catch (IOException e) {
//            System.out.println("Капец, у тебя ошибка");
//        }
//        return clientSocketInStream;
//
//        return new ByteArrayInputStream();
        return null;
    }

    public static Video fakeVideo() {
        return new Video(
                42,
                new VideoMetadata(
                        "Fake video",
                "Fake description"
                ),
                10,
                (short) 5,
                Quality.QUALITY_1080,
                "fake author"
        );
    }
}
