package ru.ssyp.youtube;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.sqlite.*;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

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

    private Map<Integer, Integer> videoSegmentAmount;

    private Videos videos;

    private PreparedDatabase db;

    private VideoSegments videoSegments;

    private Users users;

    private Channel channel;

    private Session session;


    @BeforeEach
    public void before() throws IOException, SQLException, InvalidChannelDescriptionException, InvalidChannelNameException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException {
        this.savedVideos = new HashMap<>();
        this.tempDirWithPrefix = Files.createTempDirectory("segmentated_youtube_test");
        this.videoSegmentAmount = new HashMap<>();
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        db = new SqliteDatabase(conn);
        this.videoSegments = new MemoryVideoSegments(videoSegmentAmount);
        this.videos = new SqliteVideos(db, videoSegments);
        this.youtube = new SegmentatedYoutube(
                new FakeStorage(
                        savedVideos
                ),
                Paths.get("C:\\Users\\programmer\\Downloads\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\bin\\ffmpeg.exe"),
                new MemoryVideoSegments(
                        videoSegmentAmount
                ),
                videos
        );
        users = new SqliteUsers(db, new TokenGenRandomB64(20));
        Channels channels = new SqliteChannels(db);
        session = users.getSession(users.addUser("testUser", new DummyPassword("hello")));
        channel = channels.addNew(session, "testChannel", "testChannelDescription");
    }

    @ParameterizedTest
    @ValueSource(strings = {"sample-15s.mp4" ,"totally-different-sample-15s.mp4"})
    public void testUpload(String name) throws IOException, InterruptedException, InvalidChannelIdException {
        System.out.println(tempDirWithPrefix);
        Video video = this.youtube.upload(
                new FakeUser(),
                VideoMetadata.fakeMetadata(channel.channelInfo().id()),
                new FileInputStream(Paths.get("src", "test", "resources", name).toFile())
        );
        System.out.println(this.savedVideos);
        assertEquals(24, savedVideos.size());
        assertEquals(8, videoSegmentAmount.get(video.id));
//        System.out.println(videoSegmentAmount);
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
    public void testUploadTwice() throws IOException, InterruptedException, InvalidChannelIdException {
        testUpload("sample-15s.mp4");
        testUpload("totally-different-sample-15s.mp4");
    }

}
