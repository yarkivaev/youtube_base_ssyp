package ru.ssyp.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class VideoEditingProcess implements Runnable {
    private final Path ffmpegPath;
    private final Path tmpFolder;
    private final String resolution;

    public VideoEditingProcess(Path ffmpegPath, Path tmpFolder, String resolution) {
        this.ffmpegPath = ffmpegPath;
        this.tmpFolder = tmpFolder;
        this.resolution = resolution;
    }

    @Override
    public void run() {
        Path local_file_path = Paths.get(tmpFolder.toString(), "output.mp4");

        try {
            ProcessBuilder pb = new ProcessBuilder();
            Path output_res_path = Paths.get(tmpFolder.toString(), "output_" + resolution + ".mp4");
            pb.command(
                    ffmpegPath.toString(), "-y", "-i", local_file_path.toString(),
                    "-vf", "scale=-1:" + resolution + ",setsar=1:1", "-c:v", "libx264", "-c:a",
                    "copy", output_res_path.toString()
            );
            Process process = pb.redirectErrorStream(true).start();
            InputStream ffmpeg_stream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ffmpeg_stream));

            while (true) {
                String log = reader.readLine();
                if (Objects.isNull(log))
                    break;
            }

            process.waitFor();
            ProcessBuilder pb1 = new ProcessBuilder();
            pb1.command(
                    ffmpegPath.toString(),
                    "-i", output_res_path.toString(),
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

            process2.waitFor();
            System.out.println(resolution + " cropped");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
