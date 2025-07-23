package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Quality;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;
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
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Video[] allVideos() {
        return new Video[0];
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
            System.err.println(e.getMessage());
        }
    }
}
