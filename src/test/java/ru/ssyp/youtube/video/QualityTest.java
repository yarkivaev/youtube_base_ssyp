package ru.ssyp.youtube.video;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class QualityTest {
    @Test
    void rawLowestQuality() throws IOException {
        Quality quality = Quality.QUALITY_360;
        byte[] raw = quality.rawContent().readAllBytes();
        assertArrayEquals(new byte[]{0x01}, raw);
    }

    @Test
    void rawMaxQuality() throws IOException {
        Quality quality = Quality.QUALITY_1080;
        byte[] raw = quality.rawContent().readAllBytes();
        assertArrayEquals(new byte[]{0x03}, raw);
    }
}
