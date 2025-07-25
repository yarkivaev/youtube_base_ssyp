package ru.ssyp.youtube;

import ru.ssyp.youtube.channel.ForeignChannelIdException;
import ru.ssyp.youtube.channel.InvalidChannelIdException;
import ru.ssyp.youtube.users.Session;
import ru.ssyp.youtube.video.InvalidVideoIdException;
import ru.ssyp.youtube.video.Video;
import ru.ssyp.youtube.video.VideoMetadata;
import ru.ssyp.youtube.video.Videos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.sql.SQLException;

public class SegmentatedYoutube implements Youtube {
    private final Path ffmpegPath;
    private final Storage storage;
    private final VideoSegments videoSegments;
    private final String[] resolutions;
    private final Videos videos;

    public SegmentatedYoutube(Storage storage, Path ffmpegPath, VideoSegments videoSegments, String[] resolutions, Videos videos) {
        this.storage = storage;
        this.ffmpegPath = ffmpegPath;
        this.resolutions = resolutions;
        this.videoSegments = videoSegments;
        this.videos = videos;
    }

    public SegmentatedYoutube(Storage storage, Path ffmpegPath, VideoSegments videoSegments, Videos videos) {
        this(storage, ffmpegPath, videoSegments, new String[]{"1080", "720", "360"}, videos);
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
    public Video upload(Session user, VideoMetadata metadata, InputStream stream) throws IOException, InterruptedException, InvalidChannelIdException {
        Video video = videos.addNew(user, metadata);
        Path tempDir = Files.createTempDirectory(String.valueOf(video.id));
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
                String segment_name = video.id + "_segment_" + resolution + "_" + i;
                storage.upload(segment_name, is);
            }
        }


        try {
            videoSegments.sendSegmentsAmount(video.id, segment_count);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (File file : file_list) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
        Files.delete(tempDir);

        return video;
    }

    @Override
    public void remove(int videoId, Session session) throws InvalidVideoIdException, IOException, ForeignChannelIdException {
        try {
            videos.deleteVideo(videoId, session);
            int segmentCount = videoSegments.getSegmentsAmount(videoId);
            videoSegments.deleteSegmentsAmount(videoId);
            for (String resolution : resolutions) {
                for (int i = 0; i < segmentCount; i++) {
                    String segmentName = videoId + "_segment_" + resolution + "_" + i;
                    storage.remove(segmentName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream load(int videoId, int startSegment, int resolution) throws InvalidVideoIdException {
        try {
            int segmentAmount = videoSegments.getSegmentsAmount(videoId);
            if (startSegment >= segmentAmount) {
                throw new RuntimeException("incorrect startSegment");
            }
            if (!Arrays.asList(resolutions).contains(Integer.toString(resolution))) {
                throw new RuntimeException("incorrect resolution");
            }

            String file_name = videoId + "_segment_" + resolution + "_" + startSegment;
            System.out.println(file_name);
            return storage.download(file_name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

