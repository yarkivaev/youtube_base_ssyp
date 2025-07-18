package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.Session;
import ru.ssyp.youtube.Token;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteSession implements Session {
    private final Connection conn;
    private final int id;
    private final Token token;

    public SqliteSession(Connection conn, int id, Token token) {
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
    public Token token() {
        return token;
    }
}
