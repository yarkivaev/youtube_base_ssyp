package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.MemoryVideoSegments;
import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.sql.*;
import java.util.HashMap;

public class SqliteChannelInfoTest {
    private Channel channel;
    private Session session1;
    private Session session2;
    private ChannelInfo channelInfo;
    private Videos videos;

    @BeforeEach
    void setUp() throws SQLException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException, InvalidChannelDescriptionException, InvalidChannelNameException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        PreparedDatabase db = new SqliteDatabase(conn);
        Channels channels = new SqliteChannels(db);
        Users users = new SqliteUsers(db, new TokenGenRandomB64(20));
        session1 = users.getSession(users.addUser("test_user_1", new DummyPassword("test_value_1")));
        session2 = users.getSession(users.addUser("test_user_2", new DummyPassword("test_value_2")));
        channel = channels.addNew(session1, "name", "test_description");
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT id FROM channels WHERE owner = ?;");
        selectStatement.setInt(1, session1.userId());
        ResultSet rs = selectStatement.executeQuery();
        int channelId = rs.getInt("id");
        channelInfo = new SqliteChannelInfo(channelId, db);
        VideoSegments videoSegments = new MemoryVideoSegments(new HashMap<>());
        videos = new SqliteVideos(new SqliteDatabase(conn), videoSegments);
    }

    @Test
    void getInfoTest() throws Exception {
        Assertions.assertEquals("test_description", channelInfo.description());
        Assertions.assertEquals("name", channelInfo.name());
        Assertions.assertEquals(0, channelInfo.subscribers());
        Assertions.assertEquals(session1.userId(), channelInfo.owner());
        Assertions.assertNotEquals(session2.userId(), channelInfo.owner());
        channel.subscribe(session1.userId());
        Assertions.assertEquals(1, channelInfo.subscribers());
        channel.subscribe(session2.userId());
        Assertions.assertEquals(2, channelInfo.subscribers());
        channel.unsubscribe(session1.userId());
        Assertions.assertEquals(1, channelInfo.subscribers());
        channel.unsubscribe(session2.userId());
        Assertions.assertEquals(0, channelInfo.subscribers());
        Assertions.assertEquals(0, channel.channelInfo().videoAmount());
        videos.addNew(session1, new VideoMetadata("Betty", "I really hope you're on my side, I really hope you get it", channel.channelInfo().id()));
        Assertions.assertEquals(1, channel.channelInfo().videoAmount());
        videos.deleteVideo(1, session1);
        Assertions.assertEquals(0, channel.channelInfo().videoAmount());
    }
}
