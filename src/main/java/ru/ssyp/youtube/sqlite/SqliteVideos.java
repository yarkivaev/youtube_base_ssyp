package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.VideoSegments;
import ru.ssyp.youtube.channel.ForeignChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        try {
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
                    (short) 2,
                    Quality.fromPriority(3),
                    rs.getString("owner")
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Video video(int videoId) throws InvalidVideoIdException {
        try {
            Connection dbConn = db.conn();
            int segments = videoSegments.getSegmentsAmount(videoId);
            short segmentLength = 2;
            var sql = """
                    SELECT owner, title, description, maxQuality FROM videos WHERE "videoId" = ?
                    """;

            var pstmt = dbConn.prepareStatement(sql);
            pstmt.setInt(1, videoId);
            var rs = pstmt.executeQuery();

            PreparedStatement getChannelId = dbConn.prepareStatement("SELECT channelId FROM channelsVideos WHERE videoId = ?;");
            getChannelId.setInt(1, videoId);
            ResultSet idrs = getChannelId.executeQuery();

            int channelId = idrs.getInt(1);

            while (rs.next()) {
                VideoMetadata metadata = new VideoMetadata(rs.getString("title"), rs.getString("description"), channelId);
                return new Video(videoId, metadata, () -> segments, segmentLength, Quality.fromPriority(rs.getInt("maxQuality")), rs.getString("owner"));
            }

            throw new RuntimeException("Video not found");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @Override
    public Video[] videos() {
        try {
            // written by deepseek, pray that it works
            Statement stmt = db.conn().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT v.*, cv.channelId FROM videos v LEFT JOIN channelsVideos cv ON v.videoId = cv.videoId;");

            List<Video> videos = new ArrayList<>();

            while (rs.next()) {
                int channelId = rs.getInt("channelId");
                int videoId = rs.getInt("videoId");
                String owner = rs.getString("owner");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int maxQuality = rs.getInt("maxQuality");

                videos.add(new Video(
                        videoId,
                        new VideoMetadata(title, description, channelId),
                        () -> {
                            try {
                                return videoSegments.getSegmentsAmount(videoId);
                            } catch (SQLException | InvalidVideoIdException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        (short) 2,
                        Quality.fromPriority(maxQuality),
                        owner
                ));
            }

            return videos.toArray(new Video[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void editVideo(int videoId, EditVideo edit, Session session) throws InvalidVideoIdException, ForeignChannelIdException {
        Connection dbConn = db.conn();
        try {
            PreparedStatement videoIdStatement = db.conn().prepareStatement("SELECT * FROM videos WHERE videoId = ?;");
            videoIdStatement.setInt(1, videoId);
            ResultSet videoIdRS = videoIdStatement.executeQuery();

            if (!videoIdRS.next()) {
                throw new InvalidVideoIdException();
            }
            VideoMetadata originalMetadata = new VideoMetadata(videoIdRS.getString("title"), videoIdRS.getString("description"), 0);

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
            var pstmt = dbConn.prepareStatement("UPDATE videos SET title = ?, description = ? WHERE videoId = ?;");
            pstmt.setInt(3, videoId);
            if (edit.name.isPresent()) {
                pstmt.setString(1, edit.name.orElse(originalMetadata.title));
            } else {
                pstmt.setString(1, originalMetadata.title);
            }
            if (edit.description.isPresent()) {
                pstmt.setString(2, edit.description.orElse(originalMetadata.description));
            } else {
                pstmt.setString(2, originalMetadata.description);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
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
