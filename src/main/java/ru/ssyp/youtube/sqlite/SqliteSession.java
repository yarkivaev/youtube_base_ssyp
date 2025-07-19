package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.Session;
import ru.ssyp.youtube.token.Token;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteSession implements Session {
    private final PreparedDatabase db;
    private final int userId;
    private final Token token;

    public SqliteSession(PreparedDatabase db, int userId, Token token) {
        this.db = db;
        this.userId = userId;
        this.token = token;
    }

    @Override
    public int userId() {
        return userId;
    }

    @Override
    public String username() {
        try {
            PreparedStatement statement = db.conn().prepareStatement("SELECT name FROM users WHERE id = ?;");
            statement.setInt(1, userId);
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
