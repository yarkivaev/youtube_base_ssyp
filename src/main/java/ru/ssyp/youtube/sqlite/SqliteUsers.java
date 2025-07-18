package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.PasswordHasher;
import ru.ssyp.youtube.Session;
import ru.ssyp.youtube.token.Token;
import ru.ssyp.youtube.Users;
import ru.ssyp.youtube.token.TokenGen;

import java.sql.*;

public class SqliteUsers implements Users {
    private final PasswordHasher hasher = new PasswordHasher();
    private final Connection conn;
    private final TokenGen tokenGen;

    public SqliteUsers(Connection conn, TokenGen tokenGen) {
        this.conn = conn;
        this.tokenGen = tokenGen;
    }

    public void initDatabase() throws SQLException {
        Statement statement = conn.createStatement();
        statement.executeUpdate("CREATE TABLE users (id INTEGER PRIMARY KEY, name STRING NOT NULL UNIQUE, passhash STRING NOT NULL);");
        statement.executeUpdate("CREATE TABLE sessions (id INTEGER PRIMARY KEY, token STRING NOT NULL UNIQUE, user INTEGER NOT NULL, FOREIGN KEY (user) REFERENCES users (id));");
    }

    private Token addSession(int userid) {
        Token token = tokenGen.token();

        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO sessions (token, user) VALUES (?, ?);");
            statement.setString(1, token.value);
            statement.setInt(2, userid);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return token;
    }

    @Override
    public Token addUser(String name, String password) {
        if (name.isEmpty() || password.isEmpty()) {
            return null;
        }

        if (!name.matches("^[a-zA-Z0-9_]*$")) {
            return null;
        }

        try {
            // should fail if username is taken
            PreparedStatement statement = conn.prepareStatement("INSERT INTO users (name, passhash) VALUES (?, ?);");
            statement.setString(1, name);
            statement.setString(2, hasher.hashPassword(password));
            statement.executeUpdate();
        } catch (SQLException e) {
            // TODO: different exception/return value if username is taken
            throw new RuntimeException(e);
        }

        return login(name, password);
    }

    @Override
    public Token login(String name, String password) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT id, passhash FROM users WHERE name = ?;");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return null;
            }

            int id = rs.getInt("id");
            String hash = rs.getString("passhash");

            if (!hasher.checkPassword(hash, password)) {
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
            PreparedStatement statement = conn.prepareStatement("SELECT user FROM sessions WHERE token = ?;");
            statement.setString(1, token.value);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return null;
            }

            int userid = rs.getInt("user");
            return new SqliteSession(conn, userid, token);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
