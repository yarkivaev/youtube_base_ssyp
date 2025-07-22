package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.channel.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteChannel implements Channel {

    private final int channelId;
    private final PreparedDatabase db;
    private final SqliteChannelInfo sqliteChannelInfo;

    public SqliteChannel(int channelId, PreparedDatabase db, SqliteChannelInfo sqliteChannelInfo) {
        this.channelId = channelId;
        this.db = db;
        this.sqliteChannelInfo = sqliteChannelInfo;
    }
    @Override
    public ChannelInfo channelInfo() {
        return new SqliteChannelInfo(channelId, db);
    }

    @Override
    public boolean checkSubscription(int userId) throws SQLException, InvalidUserIdException {
        PreparedStatement selectStatement1 = db.conn().prepareStatement("SELECT * FROM users WHERE id = ?;");
        selectStatement1.setInt(1, userId);
        ResultSet rs1 = selectStatement1.executeQuery();
        if (!rs1.next()){
            throw new InvalidUserIdException();
        }

        sqliteChannelInfo.subscribers();
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT * FROM subscribers WHERE user_id = ? AND channel_id = ?;");
        selectStatement.setInt(1, userId);
        selectStatement.setInt(2, channelId);
        ResultSet rs = selectStatement.executeQuery();

        return rs.next();
    }

    @Override
    public void subscribe(int userId) throws SQLException, InvalidUserIdException, AlreadySubscribedException {
        if (checkSubscription(userId)){
            throw new AlreadySubscribedException();
        }
        PreparedStatement insertStatement = db.conn().prepareStatement("INSERT INTO subscribers (user_id, channel_id) VALUES (?, ?);");
        insertStatement.setInt(1, userId);
        insertStatement.setInt(2, channelId);
        insertStatement.executeUpdate();

        PreparedStatement updateStatement = db.conn().prepareStatement("UPDATE channels SET subscribers = ? WHERE id = ?");
        updateStatement.setInt(1, sqliteChannelInfo.subscribers() + 1);
        updateStatement.setInt(2, channelId);
        updateStatement.executeUpdate();

    }

    @Override
    public void unsubscribe(int userId) throws SQLException, NotSubscribedException, InvalidUserIdException {
        if (!checkSubscription(userId)) {
            throw new NotSubscribedException();
        }
        PreparedStatement deleteStatement = db.conn().prepareStatement("DELETE FROM subscribers WHERE user_id = ? AND channel_id = ?;");
        deleteStatement.setInt(1, userId);
        deleteStatement.setInt(2, channelId);
        deleteStatement.executeUpdate();

        PreparedStatement updateStatement = db.conn().prepareStatement("UPDATE channels SET subscribers = ? WHERE id = ?");
        updateStatement.setInt(1, sqliteChannelInfo.subscribers() - 1);
        updateStatement.setInt(2, channelId);
        updateStatement.executeUpdate();
    }
}
