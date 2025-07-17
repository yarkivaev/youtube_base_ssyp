package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.User;
import ru.ssyp.youtube.Users;

import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SqliteUsers implements Users {
    private final Map<String, User> sessions;
    private final Connection conn;

    public SqliteUsers() {
        sessions = new HashMap<>();

        try {
            conn = DriverManager.getConnection("jdbc:sqlite:users.db");

            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS user (id INTEGER, name STRING, passhash STRING);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String hashPassword(String password) {
        // TODO
        return password;
    }

    private String genToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    @Override
    public String addUser(String name, String password) {
        // TODO: validate name

        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO user (name, passhash) VALUES (?, ?);");
            statement.setString(1, name);
            statement.setString(2, hashPassword(password));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String token = genToken();
        sessions.put(token, new SqliteUser(name, token));
        return token;
    }

    @Override
    public String login(String name, String password) {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM user WHERE name = ? AND passhash = ?;");
            statement.setString(1, name);
            statement.setString(2, hashPassword(password));
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String token = genToken();
        sessions.put(token, new SqliteUser(name, token));
        return token;
    }

    @Override
    public User getUser(String token) {
        return sessions.get(token);
    }
}
