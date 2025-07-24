package ru.ssyp.youtube.video;

public class VideoMetadata {
    public final String title;
    public final String description;
    public final int channelId;

    public VideoMetadata(String title, String description, int channelId) {
        this.title = title;
        this.description = description;
        this.channelId = channelId;
    }

    public static VideoMetadata fakeMetadata(String title, int channelId) {
        return new VideoMetadata(
                title,
                "Fake description",
                channelId
        );
    }
}
