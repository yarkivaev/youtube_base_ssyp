package ru.ssyp.youtube.video;

import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.video.Quality;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QualityTest {

    @Test
    void rawLowestQuality() throws IOException {
        Quality quality = Quality.QUALITY_360;
        byte[] raw = quality.rawContent().readAllBytes();
        assertTrue(Arrays.equals(new byte[] {0x01}, raw));
    }
    @Test
    void rawMaxQuality() throws IOException {
        Quality quality = Quality.QUALITY_1080;
        byte[] raw = quality.rawContent().readAllBytes();
        assertTrue(Arrays.equals(new byte[] {0x03}, raw));
    }
}
