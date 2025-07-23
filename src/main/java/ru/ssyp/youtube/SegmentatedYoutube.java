package ru.ssyp.youtube;

import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;

import java.io.InputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SegmentatedYoutube implements Youtube {

    private final Path ffmpegPath;

    private final Storage storage;

    private final VideoSegments videoSegments;

    private final String[] resolutions;

    private final Random random = new Random();


    public SegmentatedYoutube(Storage storage, Path ffmpegPath, VideoSegments videoSegments, String[] resolutions) {
        this.storage = storage;
        this.ffmpegPath = ffmpegPath;
        this.resolutions = resolutions;
        this.videoSegments = videoSegments;
    }


    public SegmentatedYoutube(Storage storage, Path ffmpegPath, VideoSegments videoSegments) {
        this(storage, ffmpegPath, videoSegments, new String[]{"1080", "720", "360"});
    }

    @Override
    public Video videoInfo(int videoId) {
        return null;
    }

    @Override
    public Video[] videos() {
        return new Video[0];
    }

    @Override
    public void upload(int videoId, Session user, VideoMetadata video, InputStream stream) throws IOException, InterruptedException {
        // int videoId = random.nextInt();
        Path tempDir = Files.createTempDirectory(String.valueOf(videoId));
        System.out.println(tempDir);
        Path local_file_path = Paths.get(tempDir.toString(), "output.mp4");
        Files.copy(stream, local_file_path);


        Thread[] threads = new Thread[resolutions.length];
        for (int i = 0; i < resolutions.length; i++) {
            VideoEditingProcess vep = new VideoEditingProcess(ffmpegPath, tempDir, resolutions[i]);
            threads[i] = new Thread(vep);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }


        File[] file_list = new File(tempDir.toString()).listFiles();
        int segment_count = (file_list.length - 1 - resolutions.length) / resolutions.length;
        System.out.println("\nsegment_count: " + segment_count);


        for (String resolution : resolutions) {
            for (int i = 0; i < segment_count; i++) {
                InputStream is = Files.newInputStream(Paths.get(tempDir.toString(), "output_" + resolution + "_" + i + ".mp4"));
                String segment_name = videoId + "_segment_" + resolution + "_" + Integer.toString(i);
                storage.upload(segment_name, is);
            }
        }


        videoSegments.sendSegmentAmount(videoId, segment_count);

        for (File file : file_list) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        Files.delete(tempDir);

    }

    @Override
    public InputStream load(int videoId, int startSegment, int resolution) {
        int segmentAmount = videoSegments.getSegmentAmount(videoId);
        if (startSegment >= segmentAmount) {
            throw new RuntimeException("incorrect startSegment");
        }
        if (!Arrays.asList(resolutions).contains(Integer.toString(resolution))) {
            throw new RuntimeException("incorrect resolution");
        }

        String file_name = videoId + "_segment_" + resolution + "_" + startSegment;
        System.out.println(file_name);
        return storage.download(file_name);
    }
}

