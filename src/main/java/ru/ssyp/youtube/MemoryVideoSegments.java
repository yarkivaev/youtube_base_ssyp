package ru.ssyp.youtube;

import java.util.Map;

public class MemoryVideoSegments implements VideoSegments {
    Map<Integer, Integer> videoSegmentAmount;

    public MemoryVideoSegments(Map<Integer, Integer> videoSegmentAmount) {
        this.videoSegmentAmount = videoSegmentAmount;
    }

    @Override
    public void sendSegmentAmount(int videoId, int segmentsAmount) {
        videoSegmentAmount.put(videoId, segmentsAmount);
    }

    @Override
    public int getSegmentAmount(int videoId) {
        return videoSegmentAmount.get(videoId);
    }
}
