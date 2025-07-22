package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.users.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteChannels implements Channels {

    private final PreparedDatabase db;

    public SqliteChannels(PreparedDatabase db){
        this.db = db;
    }
    @Override
    public Channel channel(int channelId) {
        return new SqliteChannel(channelId, db, new SqliteChannelInfo(channelId, db));
    }

    @Override
    public Channel addNew(Session session, String name, String description) throws InvalidChannelNameException, InvalidChannelDescriptionException {
        try {
            if (name.isEmpty() || !name.matches("^[a-zA-Z0-9_]*$")) {
                throw new InvalidChannelNameException();
            }

            if (description.isEmpty()) {
                throw new InvalidChannelDescriptionException();
            }

            PreparedStatement statement = db.conn().prepareStatement("INSERT INTO channels (name, description, subscribers, owner) VALUES (?, ?, ?, ?);");
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, 0);
            statement.setInt(4, session.userId());

            statement.executeUpdate();

            PreparedStatement selectStatement = db.conn().prepareStatement("SELECT id FROM channels WHERE name = ?;");
            selectStatement.setString(1, name);
            ResultSet rs = selectStatement.executeQuery();
            int channelId = rs.getInt("id");
            return new SqliteChannel(channelId, db, new SqliteChannelInfo(channelId, db));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeChannel(Session session, int channelId) throws InvalidChannelIdException, ForeignChannelIdException {
        try {
            PreparedStatement selectStatement = db.conn().prepareStatement("SELECT owner FROM channels WHERE id = ?;");
            selectStatement.setInt(1, channelId);
            ResultSet rs = selectStatement.executeQuery();

            if (!rs.next()) {
                throw new InvalidChannelIdException();
            }

            int owner = rs.getInt("owner");

            if (owner != session.userId()) {
                throw new ForeignChannelIdException();
            }

            PreparedStatement deleteStatement = db.conn().prepareStatement("DELETE FROM channels WHERE id = ?;");
            selectStatement.setInt(1, channelId);
            deleteStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
