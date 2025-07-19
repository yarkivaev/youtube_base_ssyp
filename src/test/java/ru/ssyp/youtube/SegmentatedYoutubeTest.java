package ru.ssyp.youtube;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.ssyp.youtube.auth.UnauthenticatedException;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.FakeSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SegmentatedYoutubeTest {

    private Youtube youtube;

    private Map<String, FakeStorage.SavedVideo> savedVideos;

    private Path tempDirWithPrefix;

    private Map<String, Integer> videoSegmentAmount;


    @BeforeEach
    public void before() throws IOException {
        this.savedVideos = new HashMap<>();
        this.tempDirWithPrefix = Files.createTempDirectory("segmentated_youtube_test");
        this.videoSegmentAmount = new HashMap<>();
        this.youtube = new SegmentatedYoutube(
                new FakeStorage(
                        savedVideos
                ),
                Paths.get("C:\\Users\\programmer\\Downloads\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\bin\\ffmpeg.exe"),
                tempDirWithPrefix,
                new MemoryVideoSegments(
                        videoSegmentAmount
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"sample-15s.mp4" ,"totally-different-sample-15s.mp4"})
    public void testUpload(String name) throws IOException, InterruptedException, UnauthenticatedException {
        System.out.println(tempDirWithPrefix);
        this.youtube.upload(
                new FakeSession(0, "fakeuser", new Token("faketoken")),
                "test-file",
                new FileInputStream(Paths.get("src", "test", "resources", name).toFile())
        );
        System.out.println(this.savedVideos);
        assertEquals(24, savedVideos.size());
        assertEquals(8, videoSegmentAmount.get("test-file"));
        System.out.println(videoSegmentAmount);

        HashSet set = new HashSet();
        for (var i: savedVideos.values()){
            set.add(i);
            System.out.println(i.fileSize());
            assertNotEquals(i.fileSize(), 0);
        }
        assertEquals(savedVideos.size(), set.size());
    }

    @Test
    public void testUploadTwice() throws IOException, InterruptedException, UnauthenticatedException {
        testUpload("sample-15s.mp4");
        testUpload("totally-different-sample-15s.mp4");
    }

}
