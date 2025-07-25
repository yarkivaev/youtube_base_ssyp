package ru.ssyp.youtube;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.sqlite.*;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.InvalidVideoIdException;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SegmentatedYoutubeTest {
    private Youtube youtube;
    private Map<String, FakeStorage.SavedVideo> savedVideos;
    private Path tempDirWithPrefix;
    private Map<Integer, Integer> videoSegmentAmount;
    private Channel channel;

    @BeforeEach
    public void before() throws IOException, SQLException, InvalidChannelDescriptionException, InvalidChannelNameException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException {
        savedVideos = new HashMap<>();
        tempDirWithPrefix = Files.createTempDirectory("segmentated_youtube_test");
        videoSegmentAmount = new HashMap<>();
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
<<<<<<< HEAD
        PreparedDatabase db = new SqliteDatabase(conn);
        VideoSegments videoSegments = new MemoryVideoSegments(videoSegmentAmount);
        Videos videos = new SqliteVideos(db, videoSegments);
        youtube = new SegmentatedYoutube(
=======
        db = new SqliteDatabase(conn);
        this.videoSegments = new MemoryVideoSegments(db);
        this.videos = new SqliteVideos(db, videoSegments);
        this.youtube = new SegmentatedYoutube(
>>>>>>> master
                new FakeStorage(
                        savedVideos
                ),
                Paths.get("ffmpeg"),
                new MemoryVideoSegments(
                        db
                ),
                videos
        );
        Users users = new SqliteUsers(db, new TokenGenRandomB64(20));
        Channels channels = new SqliteChannels(db);
        Session session = users.getSession(users.addUser("testUser", new DummyPassword("hello")));
        channel = channels.addNew(session, "testChannel", "testChannelDescription");
    }

    @ParameterizedTest
<<<<<<< HEAD
    @ValueSource(strings = {"sample-15s.mp4", "totally-different-sample-15s.mp4"})
    public void testUpload(String name) throws IOException, InterruptedException, InvalidChannelIdException {
        System.out.println(tempDirWithPrefix);
        Video video = youtube.upload(
                new FakeUser(),
=======
    @ValueSource(strings = {"sample-15s.mp4" ,"totally-different-sample-15s.mp4"})
    public void testUpload(String name) throws IOException, InterruptedException, InvalidChannelIdException, SQLException, InvalidVideoIdException, ForeignChannelIdException {
        System.out.println(tempDirWithPrefix);
        Video video = youtube.upload(
                session,
>>>>>>> master
                VideoMetadata.fakeMetadata(name, channel.channelInfo().id()),
                new FileInputStream(Paths.get("src", "test", "resources", name).toFile())
        );
        System.out.println(savedVideos);
        assertEquals(24, savedVideos.size());
<<<<<<< HEAD
        assertEquals(8, videoSegmentAmount.get(video.id));
        System.out.println(videoSegmentAmount);
=======
        assertEquals(8, video.segmentAmount.get());
>>>>>>> master

        HashSet<FakeStorage.SavedVideo> set = new HashSet<>();
        for (FakeStorage.SavedVideo i : savedVideos.values()) {
            set.add(i);
            System.out.println(i.fileSize());
            assertNotEquals(i.fileSize(), 0);
        }
        assertEquals(savedVideos.size(), set.size());
        savedVideos.clear();

        youtube.remove(video.id, session);
        Assertions.assertThrows(InvalidVideoIdException.class, () -> youtube.remove(video.id, session));
    }

    @Test
<<<<<<< HEAD
    public void testUploadTwice() throws Exception {
=======
    public void testUploadTwice() throws IOException, InterruptedException, InvalidChannelIdException, SQLException, InvalidVideoIdException, ForeignChannelIdException {
>>>>>>> master
        testUpload("sample-15s.mp4");
        testUpload("totally-different-sample-15s.mp4");
    }
}
