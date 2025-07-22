package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.channel.ChannelInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteChannelInfo implements ChannelInfo {

    private final int channelId;

    private final PreparedDatabase db;

    public SqliteChannelInfo(int channelId, PreparedDatabase db) {
        this.channelId = channelId;
        this.db = db;
    }

    @Override
    public String name() throws SQLException {
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT name FROM channels WHERE id = ?;");
        selectStatement.setInt(1, channelId);
        ResultSet rs = selectStatement.executeQuery();
        return rs.getString("name");
    }

    @Override
    public String description() throws SQLException {
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT description FROM channels WHERE id = ?;");
        selectStatement.setInt(1, channelId);
        ResultSet rs = selectStatement.executeQuery();
        return rs.getString("description");
    }

    @Override
    public int subscribers() throws SQLException {
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT subscribers FROM channels WHERE id = ?;");
        selectStatement.setInt(1, channelId);
        ResultSet rs = selectStatement.executeQuery();
        return rs.getInt("subscribers");
    }

    @Override
    public int owner() throws SQLException {
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT owner FROM channels WHERE id = ?;");
        selectStatement.setInt(1, channelId);
        ResultSet rs = selectStatement.executeQuery();
        return rs.getInt("owner");
    }

    @Override
    public int videoAmount() {
        return 0;
    }
}
