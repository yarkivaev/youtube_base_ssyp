package ru.ssyp.youtube;

import java.util.HashMap;
import java.util.Map;

public class MemoryVideoSegments implements VideoSegments {

    Map<String, Integer> videoSegmentAmount;

    public MemoryVideoSegments(Map<String, Integer> videoSegmentAmount) {
        this.videoSegmentAmount = videoSegmentAmount;
    }

    @Override
    public void sendSegmentAmount(String videoName, int segmentsAmount) {
        videoSegmentAmount.put(videoName, segmentsAmount);
    }

    @Override
    public Integer getSegmentAmount(String videoName) {
        return videoSegmentAmount.get(videoName);
    }



}
