package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.MemoryVideoSegments;
import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Quality;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

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
        VideoMetadata metadata = new VideoMetadata("Betty", "I really hope you're on my side, I really hope you get it");
        int id = videos.addNew(session, metadata);
        metadata = new VideoMetadata("What no one's thinking", "Just make it sound like that");
        int id2 = videos.addNew(session, metadata);
        assertNotEquals(id, id2);
    }
    @Test
    void testAllVideos(){
        for (int i = 0; i<3; i++) {
            Session session = new Session() {
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
            VideoMetadata metadata = new VideoMetadata("yandex.ru/games", "I really hope you're on my side");
            int id = videos.addNew(session, metadata);
            videoSegments.sendSegmentAmount(id, 5);
        }

        assertEquals(3, videos.allVideos().length);
    }

    @Test
    void videoInfoFetch(){
        Session session = new Session() {
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
        VideoMetadata metadata = new VideoMetadata("Betty", "I really hope you're on my side");
        int id = videos.addNew(session, metadata);
        videoSegments.sendSegmentAmount(id, 5);
        Video video = videos.video(id);
        Video expVideo = new Video(id, metadata, 5, (short) 2, Quality.QUALITY_1080, session.username());
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
        Session session = new Session() {
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
        VideoMetadata metadata = new VideoMetadata("Betty", "I really hope you're on my side");
        int id = videos.addNew(session, metadata);
        videos.deleteVideo(id);
        assertThrows(RuntimeException.class, () -> {
            videos.video(id);
        });
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
