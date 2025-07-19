package ru.ssyp.youtube;

import ru.ssyp.youtube.users.Session;

import java.io.InputStream;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SegmentatedYoutube implements Youtube {

    private final Path ffmpegPath;

    private final Storage storage;

    private final VideoSegments videoSegments;

    private final Path tmpFolder;

    private final String[] resolutions;


    public SegmentatedYoutube(Storage storage, Path ffmpegPath, Path tmpFolder, VideoSegments videoSegments, String[] resolutions) {
        this.storage = storage;
        this.ffmpegPath = ffmpegPath;
        this.tmpFolder = tmpFolder;
        this.resolutions = resolutions;
        this.videoSegments = videoSegments;
    }


    public SegmentatedYoutube(Storage storage, Path ffmpegPath, Path tmpFolder, VideoSegments videoSegments) {
        this(storage, ffmpegPath, tmpFolder, videoSegments, new String[]{"1080", "720", "360"});
    }


    @Override
    public void upload(Session user, String name, InputStream stream) throws IOException, InterruptedException {
        System.out.println(tmpFolder);
        Path local_file_path = Paths.get(tmpFolder.toString(), "output.mp4");
        Files.copy(stream, local_file_path);


        Thread[] threads = new Thread[resolutions.length];
        for (int i = 0; i < resolutions.length; i++) {
            VideoEditingProcess vep = new VideoEditingProcess(ffmpegPath, tmpFolder, resolutions[i]);
            threads[i] = new Thread(vep);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }


        File[] file_list = new File(tmpFolder.toString()).listFiles();
        int segment_count = (file_list.length - 1 - resolutions.length) / resolutions.length;
        System.out.println("\nsegment_count: " + segment_count);


        for (String resolution : resolutions) {
            for (int i = 0; i < segment_count; i++) {
                InputStream is = Files.newInputStream(Paths.get(tmpFolder.toString(), "output_" + resolution + "_" + i + ".mp4"));
                String segment_name = name + "_segment_" + resolution + "_" + Integer.toString(i);
                storage.upload(segment_name, is);
            }
        }


        videoSegments.sendSegmentAmount(name, segment_count);

        for (File file : file_list) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    @Override
    public InputStream load(Session user, String name, int startSegment, int resolution) {
        int segmentAmount = videoSegments.getSegmentAmount(name);
        if (startSegment >= segmentAmount) {
            throw new RuntimeException("incorrect startSegment");
        }
        if (!Arrays.asList(resolutions).contains(Integer.toString(resolution))) {
            throw new RuntimeException("incorrect resolution");
        }

        String file_name = name + "_segment_" + resolution + "_" + startSegment;
        System.out.println(file_name);
        return storage.download(file_name);
    }
}

//    public static void main(String[] args) throws InterruptedException {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("HELLO!");
//                    Thread.sleep(1000);
//                    System.out.println("Goodbye!");
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        };
//        Thread thread1 = new Thread(
//                runnable
//        );
//        Thread thread2 = new Thread(
//                runnable
//        );
//        thread1.start();
//        thread2.start();
//        thread1.join();
//        thread2.join();
//    }
//}
