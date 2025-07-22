package ru.ssyp.youtube.video;

import ru.ssyp.youtube.ProtocolValue;

import java.io.IOException;
import java.io.InputStream;

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
