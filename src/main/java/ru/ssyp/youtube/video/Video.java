package ru.ssyp.youtube.video;

import ru.ssyp.youtube.IntCodec;
import ru.ssyp.youtube.ProtocolValue;
import ru.ssyp.youtube.StringCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class Video implements ProtocolValue {
    public final int id;
    public final VideoMetadata metadata;
    public final Supplier<Integer> segmentAmount;
    public final short segmentLength;
    public final Quality maxQuality;
    public final String author;

    public Video(int id, VideoMetadata metadata, Supplier<Integer> segmentAmount, short segmentLength, Quality maxQuality, String author) {
        this.id = id;
        this.metadata = metadata;
        this.segmentAmount = segmentAmount;
        this.segmentLength = segmentLength;
        this.maxQuality = maxQuality;
        this.author = author;
    }

    @Override
    public InputStream rawContent() throws IOException {
        byte[] id = IntCodec.intToByte(this.id);
        byte[] channelId = IntCodec.intToByte(metadata.channelId);
        byte[] segmentAmount = IntCodec.intToByte(segmentAmount());
        byte[] segmentLength = {((byte) (this.segmentLength & 0xFF))};
        byte[] maxQuality = this.maxQuality.rawContent().readAllBytes();
        byte[] authorName = StringCodec.stringToStream(author);
        byte[] title = StringCodec.stringToStream(metadata.title);
        byte[] description = StringCodec.stringToStream(metadata.description);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(id);
        outputStream.write(channelId);
        outputStream.write(segmentAmount);
        outputStream.write(segmentLength);
        outputStream.write(maxQuality);
        outputStream.write(authorName);
        outputStream.write(title);
        outputStream.write(description);

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static Video fakeVideo() {
        return new Video(
                42,
                new VideoMetadata("Fake video", "Fake description", 123),
                () -> 10,
                (short) 5,
                Quality.QUALITY_1080,
                "fake author"
        );
    }

    public int segmentAmount() {
        return segmentAmount.get();
    }
}
