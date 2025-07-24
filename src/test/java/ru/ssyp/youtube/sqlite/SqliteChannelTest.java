package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.token.TokenGenRandomB64;
import ru.ssyp.youtube.users.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqliteChannelTest {
    private Channels channels;
    private Channel channel;
    private Session session1;
    private Session session2;

    @BeforeEach
    void setUp() throws SQLException, InvalidPasswordException, InvalidUsernameException, UsernameTakenException, InvalidTokenException, InvalidChannelDescriptionException, InvalidChannelNameException, AlreadySubscribedException, InvalidUserIdException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        PreparedDatabase db = new SqliteDatabase(conn);
        channels = new SqliteChannels(db);
        Users users = new SqliteUsers(db, new TokenGenRandomB64(20));
        session1 = users.getSession(users.addUser("test_user_1", new DummyPassword("test_value_1")));
        session2 = users.getSession(users.addUser("test_user_2", new DummyPassword("test_value_2")));
        channel = channels.addNew(session1, "name", "description");

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
}
