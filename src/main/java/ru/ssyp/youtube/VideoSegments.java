package ru.ssyp.youtube;

public interface VideoSegments {

    void sendSegmentAmount(String videoName, int segmentsAmount);
    Integer getSegmentAmount(String videoName);
}
