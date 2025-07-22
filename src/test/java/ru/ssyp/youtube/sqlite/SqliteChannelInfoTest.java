package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;

import java.sql.*;

public class SqliteChannelInfoTest {
    private Channels channels;

    private Channel channel;

    private Session session1;

    private Session session2;

    private ChannelInfo channelInfo;

    private PreparedDatabase db;


    @BeforeEach
    void setUp() throws SQLException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException, InvalidChannelDescriptionException, InvalidChannelNameException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        db = new SqliteDatabase(conn);
        channels = new SqliteChannels(db);
        Users users = new SqliteUsers(db, new TokenGenRandomB64(20));
        session1 = users.getSession(users.addUser("test_user_1", new DummyPassword("test_value_1")));
        session2 = users.getSession(users.addUser("test_user_2", new DummyPassword("test_value_2")));
        channel = channels.addNew(session1, "name", "test_description");
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT id FROM channels WHERE owner = ?;");
        selectStatement.setInt(1, session1.userId());
        ResultSet rs = selectStatement.executeQuery();
        int channelId = rs.getInt("id");
        channelInfo = new SqliteChannelInfo(channelId, db);
    }

    @Test
    void getInfoTest() throws SQLException, AlreadySubscribedException, InvalidUserIdException, NotSubscribedException {
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
    }
}
