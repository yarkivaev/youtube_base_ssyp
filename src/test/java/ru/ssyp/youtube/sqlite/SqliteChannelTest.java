package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.FileStorage;
import ru.ssyp.youtube.MemoryVideoSegments;
import ru.ssyp.youtube.SegmentatedYoutube;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqliteChannelTest {
    private PreparedDatabase db;
    private Channels channels;
    private Channel channel;
    private Session session1;
    private Session session2;

    @BeforeEach
    void setUp() throws SQLException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException, InvalidChannelDescriptionException, InvalidChannelNameException, AlreadySubscribedException, InvalidUserIdException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        db = new SqliteDatabase(conn);
        channels = new SqliteChannels(db);
        Users users = new SqliteUsers(db, new TokenGenRandomB64(20));
        session1 = users.getSession(users.addUser("test_user_1", new DummyPassword("test_value_1")));
        session2 = users.getSession(users.addUser("test_user_2", new DummyPassword("test_value_2")));
        channel = channels.addNew(session1, "name", "description");
        channel = channels.addNew(session1, "name1", "description1");
    }

    @Test
    void invalidIdTest() {
        Assertions.assertThrows(InvalidUserIdException.class, () -> channel.checkSubscription(123));
        Assertions.assertThrows(InvalidUserIdException.class, () -> channel.unsubscribe(123));
        Assertions.assertThrows(InvalidUserIdException.class, () -> channel.subscribe(123));
    }

    @Test
    void infoTest() throws SQLException {
        assertEquals("name", channel.channelInfo().name());
        assertEquals("description", channel.channelInfo().description());
    }

    @Test
    void alreadyNotSubscribedTest() throws Exception {
        channel.subscribe(session1.userId());
        Assertions.assertTrue(channel.checkSubscription(session1.userId()));
        Assertions.assertThrows(NotSubscribedException.class, () -> channel.unsubscribe(session2.userId()));
        Assertions.assertThrows(AlreadySubscribedException.class, () -> channel.subscribe(session1.userId()));
        channel.unsubscribe(session1.userId());
        Assertions.assertFalse(channel.checkSubscription(session1.userId()));
    }

    @Test
    void videosTest() throws Exception {
        MemoryVideoSegments videoSegments = new MemoryVideoSegments(db);
        Videos videos = new SqliteVideos(db, videoSegments);
        FileStorage fileStorage = new FileStorage();
        SegmentatedYoutube segmentatedYoutube = new SegmentatedYoutube(
                fileStorage,
                Paths.get("C:\\Users\\programmer\\Downloads\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\bin\\ffmpeg.exe"),
                videoSegments,
                videos
        );
        segmentatedYoutube.upload(
                session1,
                new VideoMetadata("title1", "description1", channel.channelInfo().id()),
                new FileInputStream(Paths.get("src", "test", "resources", "sample-15s.mp4").toFile())
        );

        Video[] videoList = channel.videos(1, 1);
        System.out.println(Arrays.toString(videoList));
        System.out.println(videoList.length);
        for (Video video : videoList) {
            System.out.println(video.metadata.title);
        }
    }

    @Test
    void channelsTest() throws SQLException {
        Channel[] c = channels.getUserChannel(session1.userId());
        System.out.println(c[0].channelInfo().description());
        System.out.println(c[1].channelInfo().description());
    }
}
