package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteVideos implements Videos {

    private final PreparedDatabase db;

    private final VideoSegments videoSegments;

    public SqliteVideos(PreparedDatabase db, VideoSegments videoSegments) {
        this.db = db;
        this.videoSegments = videoSegments;
    }

    @Override
    public int addNew(Session session, VideoMetadata metadata) {
        Connection dbConn = db.conn();
        String sql = """
            INSERT INTO videos(owner, title, description, maxQuality) VALUES (?, ?, ?, ?);
            """;
        try{
            dbConn.setAutoCommit(false);
            var pstmt2 = dbConn.prepareStatement(sql);
            pstmt2.setString(1, session.username());
            pstmt2.setString(2, metadata.title);
            pstmt2.setString(3, metadata.description);
            pstmt2.setInt(4, 3);
            pstmt2.executeUpdate();

            String sqlMax = "SELECT last_insert_rowid()";
            Statement statement = dbConn.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlMax);
            dbConn.commit();

            return resultSet.getInt(1);

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Video video(int videoId) {
        Connection dbConn = db.conn();
        int segments = videoSegments.getSegmentAmount(videoId);
        //int segments = 0;
        short segmentLength = 2;
        var sql = """
        SELECT owner, title, description, maxQuality FROM videos WHERE "videoId" = ?
        """;

        try {
            var pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, videoId);
            var rs = pstmt.executeQuery();
            boolean first = true;
            while (rs.next()) {
                Quality quality;
                first = false;
                VideoMetadata metadata = new VideoMetadata(rs.getString("title"), rs.getString("description"));
                return new Video(videoId, metadata, segments, segmentLength, Quality.fromPriority(rs.getInt("maxQuality")), rs.getString("owner"));
            }
            throw new RuntimeException("Video not found");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void editVideo(int videoId, EditVideo edit) {
        Connection dbConn = db.conn();
        if(edit.name.isPresent()){
            var sql = """
        UPDATE videos
        SET title = ?
        WHERE videoId = ?;
        """;

            try {
                var pstmt = dbConn.prepareStatement(sql);
                pstmt.setInt(2, videoId);
                pstmt.setString(2, edit.name.get());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(edit.description.isPresent()){
            var sql = """
        UPDATE videos
        SET description = ?
        WHERE videoId = ?;
        """;

            try {
                var pstmt = dbConn.prepareStatement(sql);
                pstmt.setInt(2, videoId);
                pstmt.setString(2, edit.description.get());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(edit.data.isPresent()){ //Call something??? to edit the data and change max quality???
            System.err.print("Unimplemented part of the method: edit data");
        }
        return;
    }

    public void deleteVideo(int videoId){
        var sql = """
        DELETE FROM videos WHERE "videoId" = ?
        """;
        Connection dbConn = db.conn();
        try {
            var pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, videoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
