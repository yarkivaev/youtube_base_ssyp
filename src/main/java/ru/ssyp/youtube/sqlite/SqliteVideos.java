package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.channel.ForeignChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.*;

import java.sql.*;

public class SqliteVideos implements Videos {

    private final PreparedDatabase db;

    private final VideoSegments videoSegments;

    public SqliteVideos(PreparedDatabase db, VideoSegments videoSegments) {
        this.db = db;
        this.videoSegments = videoSegments;
    }

    @Override
    public Video addNew(Session session, VideoMetadata metadata) throws InvalidChannelIdException {
        Connection dbConn = db.conn();
        String sql = """
            INSERT INTO videos(owner, title, description, maxQuality) VALUES (?, ?, ?, ?);
            """;
        try{
            PreparedStatement selectStatement = db.conn().prepareStatement(
                """
                SELECT u.name as owner
                FROM channels c
                JOIN users u on c.owner = u.id
                WHERE c.id = ?;
                """
            );
            selectStatement.setInt(1, metadata.channelId);
            ResultSet rs = selectStatement.executeQuery();

            if (!rs.next()) {
                throw new InvalidChannelIdException();
            }


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
            int videoId = resultSet.getInt(1);

            PreparedStatement channelStatement = dbConn.prepareStatement("INSERT INTO channelsVideos(channelId, videoId) VALUES (?, ?);");
            channelStatement.setInt(1, metadata.channelId);
            channelStatement.setInt(2, videoId);
            channelStatement.executeUpdate();

            dbConn.commit();
            return new Video(
                    videoId,
                    metadata,
                    () -> {
                        try {
                            return videoSegments.getSegmentsAmount(videoId);
                        } catch (SQLException | InvalidVideoIdException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    (short)2,
                    Quality.fromPriority(3),
                    rs.getString("owner")
            );

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Video video(int videoId) throws SQLException, InvalidVideoIdException {
        Connection dbConn = db.conn();
        int segments = videoSegments.getSegmentsAmount(videoId);
        //int segments = 0;
        short segmentLength = 2;
        var sql = """
        SELECT owner, title, description, maxQuality FROM videos WHERE "videoId" = ?
        """;

        try {
            var pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, videoId);
            var rs = pstmt.executeQuery();

            PreparedStatement getChannelId = dbConn.prepareStatement("SELECT channelId FROM channelsVideos WHERE videoId = ?;");
            getChannelId.setInt(1, videoId);
            ResultSet idrs = getChannelId.executeQuery();

            int channelId = idrs.getInt(1);

            boolean first = true;
            while (rs.next()) {
                Quality quality;
                first = false;
                VideoMetadata metadata = new VideoMetadata(rs.getString("title"), rs.getString("description"), channelId);
                return new Video(videoId, metadata, () -> segments, segmentLength, Quality.fromPriority(rs.getInt("maxQuality")), rs.getString("owner"));
            }
            throw new RuntimeException("Video not found");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void deleteVideo(int videoId, Session session) throws InvalidVideoIdException, ForeignChannelIdException {
        var sql = """
        DELETE FROM videos WHERE "videoId" = ?
        """;
        Connection dbConn = db.conn();
        try {
            PreparedStatement videoIdStatement = db.conn().prepareStatement("SELECT * FROM videos WHERE videoId = ?;");
            videoIdStatement.setInt(1, videoId);
            ResultSet videoIdRS = videoIdStatement.executeQuery();

            if (!videoIdRS.next()) {
                throw new InvalidVideoIdException();
            }

            PreparedStatement getChannelId = db.conn().prepareStatement("SELECT channelId FROM channelsVideos WHERE videoId = ?;");
            getChannelId.setInt(1, videoId);
            ResultSet channelIdRS = getChannelId.executeQuery();

            int channelId = channelIdRS.getInt(1);

            PreparedStatement ownerStatement = db.conn().prepareStatement("SELECT owner FROM channels WHERE id = ?;");
            ownerStatement.setInt(1, channelId);
            ResultSet ownerRS = ownerStatement.executeQuery();

            int owner = ownerRS.getInt("owner");

            if (owner != session.userId()) {
                throw new ForeignChannelIdException();
            }

            var pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, videoId);
            pstmt.executeUpdate();

            PreparedStatement deleteStatement = dbConn.prepareStatement("DELETE FROM channelsVideos WHERE videoId = ?;");
            deleteStatement.setInt(1, videoId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
