package ru.ssyp.youtube.sqlite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteDatabaseTest {
    private SqliteDatabase db;

    @BeforeEach
    void setUp() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        db = new SqliteDatabase(conn);
    }

    @Test
    void testDoubleInit() {
        SqliteDatabase db2 = new SqliteDatabase(db.conn());
        Assertions.assertDoesNotThrow(db2::conn);
    }

    @Test
    void testTables() {
        Assertions.assertDoesNotThrow(() -> {
            Statement statement = db.conn().createStatement();
            statement.executeUpdate("INSERT INTO users (id, name, passhash) VALUES (1, 'abc', 'def');");
            statement.executeUpdate("INSERT INTO sessions (token, user) VALUES ('ghi', 1);");
        });
    }
}
