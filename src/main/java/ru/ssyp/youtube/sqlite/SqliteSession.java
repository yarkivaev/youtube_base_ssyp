package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteSession implements Session {
    private final Connection conn;
    private final int id;
    private final String token;

    public SqliteSession(Connection conn, int id, String token) {
        this.conn = conn;
        this.id = id;
        this.token = token;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String username() {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT name FROM users WHERE id = ?;");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("session has invalid user id");
            }

            return rs.getString("name");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String token() {
        return token;
    }
}
