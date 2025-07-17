package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.User;
import ru.ssyp.youtube.Users;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

    private byte[] pbkdf2(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private String hashPassword(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(salt) + "$" + encoder.encodeToString(pbkdf2(password, salt));
    }

    private boolean checkPassword(String hash, String password) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = hash.split(Pattern.quote("$"), 1);

        byte[] salt = decoder.decode(parts[0]);
        byte[] hashBytes = decoder.decode(parts[1]);

        return Arrays.equals(pbkdf2(password, salt), hashBytes);
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
            PreparedStatement statement = conn.prepareStatement("SELECT passhash FROM user WHERE name = ?;");
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String hash = rs.getString("passhash");
            if (!checkPassword(hash, password)) {
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
