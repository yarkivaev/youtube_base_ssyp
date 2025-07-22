package ru.ssyp.youtube.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteDatabase implements PreparedDatabase {
    private final Connection connection;
    private boolean initialized = false;

    public SqliteDatabase(Connection connection) {
        this.connection = connection;
    }

    private void init() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name STRING NOT NULL UNIQUE, passhash STRING NOT NULL);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS sessions (id INTEGER PRIMARY KEY, token STRING NOT NULL UNIQUE, user INTEGER NOT NULL, FOREIGN KEY (user) REFERENCES users (id));");
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS channels (
                    id INTEGER PRIMARY KEY,
                    name STRING NOT NULL UNIQUE,
                    description STRING NOT NULL,
                     subscribers INTEGER,
                     owner INTEGER NOT NULL
                );
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS subscribers (
                    user_id INTEGER NOT NULL REFERENCES users(id),
                    channel_id INTEGER NOT NULL REFERENCES channels(id),
                    PRIMARY KEY (user_id, channel_id)
                );
                """);


            initialized = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection conn() {
        if (!initialized) {
            init();
        }

        return this.connection;
    }
}
