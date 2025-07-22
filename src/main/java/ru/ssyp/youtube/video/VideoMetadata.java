package ru.ssyp.youtube.video;

public class VideoMetadata {

    public final String title;

    public final String description;


    public VideoMetadata(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public static VideoMetadata fakeMetadata() {
        return new VideoMetadata(
                "Fake video",
                "Fake description"
        );
    }
}
