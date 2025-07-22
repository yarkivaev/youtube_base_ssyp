package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.MemoryVideoSegments;
import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteVideosTest {

    private Videos videos;

    private VideoSegments videoSegments;

    @BeforeEach
    void setUp() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        videoSegments =  new MemoryVideoSegments(new HashMap<>());
        videos = new SqliteVideos(new SqliteDatabase(conn), videoSegments);
    }

    @Test
    void videoIdCreation(){
        int id = videos.addNew(fakeSesion(), fakeMetadata());
        VideoMetadata metadata = new VideoMetadata("What no one's thinking", "Just make it sound like that");
        int id2 = videos.addNew(fakeSesion(), metadata);
        assertNotEquals(id, id2);
    }

    @Test
    void videoInfoFetch(){
        int id = videos.addNew(fakeSesion(), fakeMetadata());
        videoSegments.sendSegmentAmount(id, 5);
        Video video = videos.video(id);
        Video expVideo = new Video(id, fakeMetadata(), 5, (short) 2, Quality.QUALITY_1080, fakeSesion().username());
        assertTrue(videosAreEqual(video, expVideo));
    }

    @Test
    void invalidIndex(){
        assertThrows(RuntimeException.class, () -> {
            videos.video(1);
        });
    }

    @Test
    void videoDelete(){
        int id = videos.addNew(fakeSesion(), fakeMetadata());
        videos.deleteVideo(id);
        assertThrows(RuntimeException.class, () -> {
            videos.video(id);
        });
    }

    @Test
    void videoEdit(){
        int vIDeo = videos.addNew(fakeSesion(), fakeMetadata());
        EditVideo edit = new EditVideo(Optional.of("Test1"), Optional.of(""), Optional.of(InputStream.nullInputStream()));
        videos.editVideo(vIDeo, edit);
        Video video = videos.video(vIDeo);
        assertEquals(video.metadata.title, "Test1");
        assertEquals(video.metadata.description, fakeMetadata().description);
    }

    @Test
    void videoEditTitleAndDescription(){
        int vIDeo = videos.addNew(fakeSesion(), fakeMetadata());
        EditVideo edit = new EditVideo(Optional.of("Test1"), Optional.of("Next time you see your folks at dinner"), Optional.of(InputStream.nullInputStream()));
        videos.editVideo(vIDeo, edit);
        Video video = videos.video(vIDeo);
        assertEquals(video.metadata.title, "Test1");
        assertEquals(video.metadata.description, "Next time you see your folks at dinner");
    }

    private VideoMetadata fakeMetadata(){
        return new VideoMetadata("Test", "Description");
    }

    private Session fakeSesion() {
        return new Session() {
            @Override
            public int userId() {
                return 5;
            }

            @Override
            public String username() {
                return "Joe";
            }

            @Override
            public Token token() {
                return new Token("jn43kjnvDVs");
            }
        };
    }

    private static boolean videosAreEqual(Video video, Video expVideo) {
        return video.id == expVideo.id &&
                Objects.equals(video.author, expVideo.author) &&
                video.maxQuality == expVideo.maxQuality &&
                video.segmentAmount == expVideo.segmentAmount &&
                video.segmentLength == expVideo.segmentLength &&
                Objects.equals(video.metadata.title, expVideo.metadata.title) &&
                Objects.equals(video.metadata.description, expVideo.metadata.description);
    }
}
