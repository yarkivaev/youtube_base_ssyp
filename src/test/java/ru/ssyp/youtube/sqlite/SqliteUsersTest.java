package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ssyp.youtube.Session;
import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.password.DummyPassword;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.token.TokenGen;
import ru.ssyp.youtube.token.TokenGenRandomB64;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteUsersTest {
    private TokenGen tokenGen;
    private SqliteUsers users;

    @BeforeEach
    void setUp() throws SQLException {
        tokenGen = new TokenGenRandomB64(20);

        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        users = new SqliteUsers(new SqliteDatabase(conn), tokenGen);
    }

    @Test
    void testInvalidUsernameOrPassword() {
        Password pass = new DummyPassword("123");

        Assertions.assertNull(users.addUser("this has spaces", pass));
        Assertions.assertNull(users.addUser("русские_буквы", pass));
        Assertions.assertNull(users.addUser("!\"№;%:&*()=+-,<.>/?\\|", pass));

        Password empty = new DummyPassword("");
        Assertions.assertNull(users.addUser("valid_username", empty));
    }

    @Test
    void testRegister() {
        Password pass = new DummyPassword("1");
        Password wrong = new DummyPassword("wrongpassword");

        Token token1 = users.addUser("testuser", pass);
        Assertions.assertNotNull(token1);

        Session session1 = users.getSession(token1);
        Assertions.assertNotNull(session1);
        Assertions.assertEquals("testuser", session1.username());
        Assertions.assertEquals(token1, session1.token());

        Assertions.assertNull(users.login("testuser", wrong));

        Token token2 = users.login("testuser", pass);
        Assertions.assertNotEquals(token1, token2);

        Session session2 = users.getSession(token2);
        Assertions.assertEquals(session1.userId(), session2.userId());
        Assertions.assertNotNull(session2);
        Assertions.assertEquals("testuser", session2.username());
        Assertions.assertEquals(token2, session2.token());
    }

    @Test
    void testNonexistentUser() {
        Assertions.assertNull(users.login("not_real", new DummyPassword("what password?")));
    }

    @Test
    void testNonexistentSession() {
        Assertions.assertNull(users.getSession(tokenGen.token()));
    }

    @Test
    void testTakenUsername() {
        Assertions.assertNotNull(users.addUser("user", new DummyPassword("correct horse battery staple")));
        Assertions.assertThrows(RuntimeException.class, () -> users.addUser("user", new DummyPassword("12345")));
    }
}
