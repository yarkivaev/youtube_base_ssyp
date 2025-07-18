package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.Session;
import ru.ssyp.youtube.token.Token;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteSession implements Session {
    private final PreparedDatabase db;
    private final int id;
    private final Token token;

    public SqliteSession(PreparedDatabase db, int id, Token token) {
        this.db = db;
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
            PreparedStatement statement = db.conn().prepareStatement("SELECT name FROM users WHERE id = ?;");
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
