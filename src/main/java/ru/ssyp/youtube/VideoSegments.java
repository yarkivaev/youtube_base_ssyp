package ru.ssyp.youtube;

import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.video.InvalidVideoIdException;

import java.sql.SQLException;

public interface VideoSegments {

    void sendSegmentsAmount(int videoId, int segmentsAmount) throws SQLException;
    int getSegmentsAmount(int videoId) throws SQLException, InvalidVideoIdException;

    void deleteSegmentsAmount(int videoId) throws SQLException, InvalidVideoIdException;
}
