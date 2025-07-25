package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.MemoryVideoSegments;
import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.*;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteVideosTest {

    private Videos videos;

    private VideoSegments videoSegments;

    private Channels channels;

    private Channel channel1;

    private Channel channel2;

    private PreparedDatabase db;

    private Session session;

    private VideoMetadata fakeMetadata;


    @BeforeEach
    void setUp() throws SQLException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException, InvalidChannelDescriptionException, InvalidChannelNameException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        db = new SqliteDatabase(conn);
        channels = new SqliteChannels(db);
        Users users = new SqliteUsers(db, new TokenGenRandomB64(20));
        fakeMetadata = new VideoMetadata("2085", "I'd hate to have to die", 1);
        session = users.getSession(users.addUser("test_user_1", new DummyPassword("test_value_1")));
        channel1 = channels.addNew(session, "name", "description");
        channel2 = channels.addNew(session, "name2", "description2");
        videoSegments =  new MemoryVideoSegments(db);
        videos = new SqliteVideos(new SqliteDatabase(conn), videoSegments);
    }

    @Test
    void videoIdCreation() throws InvalidChannelIdException {
        Session session = new Session() {
            @Override
            public int userId() {
                return 2;
            }

            @Override
            public String username() {
                return "Anton";
            }

            @Override
            public Token token() {
                return new Token("wkefosijno3");
            }
        };
        VideoMetadata metadata = new VideoMetadata("Betty", "I really hope you're on my side, I really hope you get it", channel1.channelInfo().id());
        Video video1 = videos.addNew(session, metadata);
        VideoMetadata finalMetadata = metadata;
        Assertions.assertThrows(RuntimeException.class, () -> videos.addNew(session, finalMetadata));
        metadata = new VideoMetadata("What no one's thinking", "Just make it sound like that", channel1.channelInfo().id());
        Video video2 = videos.addNew(session, metadata);
        assertNotEquals(video1.id, video2.id);
        // TODO: проверить, добавилось ли видео в канал
    }

    @Test
    void videoInfoFetch() throws InvalidChannelIdException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException, SQLException, InvalidVideoIdException {
        VideoMetadata metadata = new VideoMetadata("Betty", "I really hope you're on my side", channel1.channelInfo().id());
        Video video = videos.addNew(session, metadata);
        videoSegments.sendSegmentsAmount(video.id, 5);
        Video expVideo = new Video(video.id, metadata, () -> 5, (short) 2, Quality.QUALITY_1080, session.username());

        assertTrue(videosAreEqual(video, expVideo));
        assertEquals(5, videoSegments.getSegmentsAmount(video.id));
        videoSegments.deleteSegmentsAmount(video.id);
        Assertions.assertThrows(InvalidVideoIdException.class, () -> videoSegments.getSegmentsAmount(video.id));
        Assertions.assertThrows(InvalidVideoIdException.class, () -> videoSegments.deleteSegmentsAmount(video.id));
    }

    @Test
    void videoInfoEdit() throws InvalidChannelIdException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException, InvalidVideoIdException, ForeignChannelIdException, SQLException {
        Video video = videos.addNew(session, fakeMetadata);
        videoSegments.sendSegmentsAmount(video.id, 5);
        EditVideo edit = new EditVideo(Optional.of("Test1"), Optional.ofNullable("").filter(Predicate.not(s -> true)), Optional.of(new ByteArrayInputStream( "Hello!".getBytes())));
        videos.editVideo(video.id, edit, session);
        video = videos.video(video.id);
        System.out.println("");
        System.out.println(video.metadata.title);
        System.out.println(video.metadata.description);
        Video expVideo = new Video(video.id, new VideoMetadata("Test1", String.valueOf(video.metadata.description), video.metadata.channelId), () -> 5, (short) 2, Quality.QUALITY_1080, session.username());
        assertTrue(videosAreEqual(video, expVideo));
        edit = new EditVideo(Optional.ofNullable("").filter(Predicate.not(s -> true)), Optional.of("Lemons"), Optional.of(new ByteArrayInputStream( "Hello!".getBytes())));
        videos.editVideo(video.id, edit, session);
        Video exp2video = new Video(video.id, new VideoMetadata("Test1", "Lemons", 5), () -> 5, (short) 2, Quality.QUALITY_1080, session.username());
        video = videos.video(video.id);
        System.out.println("");
        System.out.println(video.metadata.title);
        System.out.println(video.metadata.description);
        assertTrue(videosAreEqual(video, exp2video));
    }

    @Test
    void invalidIndex(){
        assertThrows(InvalidVideoIdException.class, () -> {
            videos.video(1);
        });
    }

    @Test
    void videoDelete() throws InvalidChannelIdException, InvalidVideoIdException, ForeignChannelIdException {
        Session wrongSession = new Session() {
            @Override
            public int userId() {
                return 4;
            }

            @Override
            public String username() {
                return "Someone";
            }

            @Override
            public Token token() {
                return new Token("pbkdfpbkdfpbkdf");
            }
        };
        Video video = videos.addNew(session, fakeMetadata);
        Assertions.assertThrows(InvalidVideoIdException.class, () -> videos.deleteVideo(10, session));
        Assertions.assertThrows(ForeignChannelIdException.class, () -> videos.deleteVideo(video.id, wrongSession));
        videos.deleteVideo(video.id, session);
        Assertions.assertThrows(InvalidVideoIdException.class, () -> videos.deleteVideo(video.id, session));
        assertThrows(InvalidVideoIdException.class, () -> {
            videos.video(video.id);
        });
    }

    private static boolean videosAreEqual(Video video, Video expVideo) {
        return video.id == expVideo.id &&
                Objects.equals(video.author, expVideo.author) &&
                video.maxQuality == expVideo.maxQuality &&
                video.segmentAmount() == expVideo.segmentAmount() &&
                video.segmentLength == expVideo.segmentLength &&
                Objects.equals(video.metadata.title, expVideo.metadata.title) &&
                Objects.equals(video.metadata.description, expVideo.metadata.description);
    }
}
