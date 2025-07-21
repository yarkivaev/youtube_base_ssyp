package ru.ssyp.youtube;

public interface VideoSegments {

    void sendSegmentAmount(int videoId, int segmentsAmount);
    Integer getSegmentAmount(int videoId);
}
