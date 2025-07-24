package ru.ssyp.youtube;

public interface VideoSegments {
    void sendSegmentAmount(int videoId, int segmentsAmount);
    int getSegmentAmount(int videoId);
}
