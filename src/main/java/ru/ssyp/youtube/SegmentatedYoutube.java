package ru.ssyp.youtube;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        this(storage, ffmpegPath, tmpFolder,  videoSegments, new String[] {"1080", "720", "360"});
    }



    @Override
    public void upload(User user, String name, InputStream stream) throws IOException, InterruptedException {
        System.out.println(tmpFolder);
        Path local_file_path = Paths.get(tmpFolder.toString(), "output.mp4");
        Files.copy(stream, local_file_path);

        for (String resolution : resolutions) {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(
                    ffmpegPath.toString(), "-y", "-i", local_file_path.toString(),
                    "-vf", "scale=-1:" + resolution + ",setsar=1:1", "-c:v", "libx264", "-c:a",
                    "copy", Paths.get(tmpFolder.toString(), "output_" + resolution + ".mp4").toString()
            );
            Process process = pb.redirectErrorStream(true).start();
            InputStream ffmpeg_stream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpeg_stream));

            while (true) {
                String log = reader.readLine();
                if (Objects.isNull(log))
                    break;
            }
            int ret = process.waitFor();
            ProcessBuilder pb1 = new ProcessBuilder();
            pb1.command(
                    ffmpegPath.toString(),
                    "-i", Paths.get(tmpFolder.toString(), "output_" + resolution + ".mp4").toString(),
                    "-force_key_frames", "\"expr:gte(t,n_forced*1)\"",
                    "-c:v", "libx264", "-preset", "fast", "-c:a", "aac",
                    "-f", "segment", "-segment_time", "2", "-reset_timestamps", "1",
                    Paths.get(tmpFolder.toString(), "output_" + resolution + "_%d.mp4").toString()
            );
            Process process2 = pb1.redirectErrorStream(true).start();
            ffmpeg_stream = process2.getInputStream();
            reader = new BufferedReader(new InputStreamReader(ffmpeg_stream));

            while (true) {
                String log = reader.readLine();
                if (Objects.isNull(log))
                    break;
            }
            int ret2 = process2.waitFor();
            System.out.println(resolution + " cropped");
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

        for(File file: file_list) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }

    @Override
    public InputStream load (User user, String name, int startSegment, int resolution){
        String file_name = name + "_segment_" + resolution + "_" + startSegment;
        System.out.println(file_name);
        return storage.download(file_name);
    }
}
