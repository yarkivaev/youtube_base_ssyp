package ru.ssyp.youtube.video;

import ru.ssyp.youtube.ProtocolValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public enum Quality implements ProtocolValue {
    QUALITY_1080(1080, 3),
    QUALITY_720(720,2),
    QUALITY_360(360, 1);

    public final int resolution;

    public final int priority;

    Quality(int number, int priority) {
        this.resolution = number;
        this.priority = priority;
    }

    public static Quality fromPriority(int priority) {
        return switch (priority) {
            case 1 -> QUALITY_360;
            case 2 -> QUALITY_720;
            case 3 -> QUALITY_1080;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public InputStream rawContent() {
        return new ByteArrayInputStream(new byte[] {(byte)priority});
    }
}
