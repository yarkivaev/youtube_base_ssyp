package ru.ssyp.youtube;

import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.sqlite.PreparedDatabase;
import ru.ssyp.youtube.video.InvalidVideoIdException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MemoryVideoSegments implements VideoSegments {

    private final PreparedDatabase db;

    public MemoryVideoSegments(PreparedDatabase db) {
        this.db = db;
    }

    @Override
    public void sendSegmentsAmount(int videoId, int segmentsAmount) throws SQLException {
        PreparedStatement insertStatement = db.conn().prepareStatement("INSERT INTO videoSegmentsAmount (videoId, segmentsAmount) VALUES (?, ?);");
        insertStatement.setInt(1, videoId);
        insertStatement.setInt(2, segmentsAmount);
        insertStatement.executeUpdate();
    }

    @Override
    public int getSegmentsAmount(int videoId) throws SQLException, InvalidVideoIdException {
        PreparedStatement selectStatement = db.conn().prepareStatement("SELECT segmentsAmount FROM videoSegmentsAmount WHERE videoId = ?;");
        selectStatement.setInt(1, videoId);
        ResultSet rs = selectStatement.executeQuery();

        if (!rs.next()) {
            throw new InvalidVideoIdException();
        }
        return rs.getInt(1);
    }

    @Override
    public void deleteSegmentsAmount(int videoId) throws SQLException, InvalidVideoIdException {
        getSegmentsAmount(videoId);
        PreparedStatement deleteStatement = db.conn().prepareStatement("DELETE FROM videoSegmentsAmount WHERE videoId = ?;");
        deleteStatement.setInt(1, videoId);
        deleteStatement.executeUpdate();
    }


}
