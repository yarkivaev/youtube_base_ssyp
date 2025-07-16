package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.User;
import ru.ssyp.youtube.Users;

import java.sql.*;

public class SqliteUsers implements Users {
    public static final Connection conn;

    static {
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

    @Override
    public User addUser(String name, String password) {
        // TODO: validate name

        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO user (name, passhash) VALUES (?, ?);");
            statement.setString(1, name);
            statement.setString(2, hashPassword(password));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new SqliteUser(name);
    }

    @Override
    public User login(String name, String password) {
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

        return new SqliteUser(name);
    }
}
