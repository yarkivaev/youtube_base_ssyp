package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;

import java.sql.*;

public class SqliteChannelsTest {

    private Channels channels;

    private Session session1;

    private Session session2;

    private PreparedDatabase db;


    @BeforeEach
    void setUp() throws SQLException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        db = new SqliteDatabase(conn);
        channels = new SqliteChannels(db);
        Users users = new SqliteUsers(db, new TokenGenRandomB64(20));
        session1 = users.getSession(users.addUser("test_user_1", new DummyPassword("test_value_1")));
        session2 = users.getSession(users.addUser("test_user_2", new DummyPassword("test_value_2")));
    }

    @Test
    void addNewTest(){
        Assertions.assertThrows(InvalidChannelNameException.class, () -> channels.addNew(session1, "na me", "description"));
        Assertions.assertThrows(InvalidChannelNameException.class, () -> channels.addNew(session1, "имя", "description"));
        Assertions.assertThrows(InvalidChannelNameException.class, () -> channels.addNew(session1, "name!", "description"));
        Assertions.assertThrows(InvalidChannelDescriptionException.class, () -> channels.addNew(session1, "name", ""));
    }

    @Test
    void removeChannelTest() throws InvalidChannelDescriptionException, InvalidChannelNameException, SQLException, ForeignChannelIdException, InvalidChannelIdException {
        Assertions.assertThrows(InvalidChannelIdException.class, () -> channels.removeChannel(session1, 123));
        channels.addNew(session1, "name", "description");
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT id FROM channels WHERE owner = ?;");
        selectStatement.setInt(1, session1.userId());
        ResultSet rs = selectStatement.executeQuery();
        int channelId = rs.getInt("id");
        Assertions.assertThrows(ForeignChannelIdException.class, () -> channels.removeChannel(session2, channelId));
    }

}
