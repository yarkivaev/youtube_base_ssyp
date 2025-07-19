package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.Session;
import ru.ssyp.youtube.password.Password;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.Users;
import ru.ssyp.youtube.token.TokenGen;

import java.sql.*;

public class SqliteUsers implements Users {
    private final PreparedDatabase db;
    private final TokenGen tokenGen;

    public SqliteUsers(PreparedDatabase db, TokenGen tokenGen) {
        this.db = db;
        this.tokenGen = tokenGen;
    }

    private Token addSession(int userid) {
        Token token = tokenGen.token();

        try {
            PreparedStatement statement = db.conn().prepareStatement("INSERT INTO sessions (token, user) VALUES (?, ?);");
            statement.setString(1, token.value);
            statement.setInt(2, userid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return token;
    }

    @Override
    public Token addUser(String name, Password password) {
        if (name.isEmpty() || password.value().isEmpty()) {
            return null;
        }

        if (!name.matches("^[a-zA-Z0-9_]*$")) {
            return null;
        }

        try {
            // should fail if username is taken
            PreparedStatement statement = db.conn().prepareStatement("INSERT INTO users (name, passhash) VALUES (?, ?);");
            statement.setString(1, name);
            statement.setString(2, password.hash());
            statement.executeUpdate();
        } catch (SQLException e) {
            // TODO: different exception/return value if username is taken
            throw new RuntimeException(e);
        }

        return login(name, password);
    }

    @Override
    public Token login(String name, Password password) {
        try {
            PreparedStatement statement = db.conn().prepareStatement("SELECT id, passhash FROM users WHERE name = ?;");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return null;
            }

            int id = rs.getInt("id");
            String hash = rs.getString("passhash");

            if (!password.check(hash)) {
                return null;
            }

            return addSession(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Session getSession(Token token) {
        try {
            PreparedStatement statement = db.conn().prepareStatement("SELECT user FROM sessions WHERE token = ?;");
            statement.setString(1, token.value);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return null;
            }

            int userid = rs.getInt("user");
            return new SqliteSession(db, userid, token);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
