package ru.ssyp.youtube.sqlite;

import ru.ssyp.youtube.channel.*;
import ru.ssyp.youtube.video.Quality;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
    public boolean checkSubscription(int userId) throws InvalidUserIdException {
        try {
            PreparedStatement selectStatement1 = db.conn().prepareStatement("SELECT * FROM users WHERE id = ?;");
            selectStatement1.setInt(1, userId);
            ResultSet rs1 = selectStatement1.executeQuery();
            if (!rs1.next()) {
                throw new InvalidUserIdException();
            }

            sqliteChannelInfo.subscribers();
            PreparedStatement selectStatement = db.conn().prepareStatement("SELECT * FROM subscribers WHERE user_id = ? AND channel_id = ?;");
            selectStatement.setInt(1, userId);
            selectStatement.setInt(2, channelId);
            ResultSet rs = selectStatement.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    public void subscribe(int userId) throws InvalidUserIdException, AlreadySubscribedException {
        try {
            if (checkSubscription(userId)) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void unsubscribe(int userId) throws NotSubscribedException, InvalidUserIdException {
        try {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Video[] videos(int startId, int amount) {
        try {
            PreparedStatement selectStatement = db.conn().prepareStatement(
                    """
                            SELECT v.videoId as videoId,
                                   v.owner as owner,
                                   v.title as title,
                                   v.description as description,
                                   v.maxQuality as maxQuality,
                                   cv.channelId as channelId,
                                   vsa.segmentsAmount as segmentsAmount
                            FROM channelsVideos cv
                            JOIN videos v on v.videoId = cv.videoId
                            JOIN videoSegmentsAmount vsa on v.videoId = vsa.videoId
                            WHERE channelId = ?
                            ORDER BY videoId
                            LIMIT ?
                            OFFSET ?;
                            """
            );
            selectStatement.setInt(1, sqliteChannelInfo.id());
            selectStatement.setInt(2, amount);
            selectStatement.setInt(3, startId - 1);
            ResultSet rs = selectStatement.executeQuery();
            List<Video> videos = new ArrayList<>();
            while (rs.next()) {
                final int segmentsAmount = rs.getInt("segmentsAmount");
                videos.add(new Video(
                        rs.getInt("videoId"),
                        new VideoMetadata(
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getInt("channelId")
                        ),
                        () -> segmentsAmount,
                        (short) 2,
                        Quality.fromPriority(3), // May be a problem
                        rs.getString("owner")
                ));
            }
            return videos.toArray(new Video[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
