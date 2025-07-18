package ru.ssyp.youtube;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SegmentatedYoutube implements Youtube {

    Map<String, Integer> map = new HashMap<>();

    private final Path ffmpegPath;

    private final Storage storage;

    private final Path tmpFolder;

    public SegmentatedYoutube(Storage storage, Path ffmpegPath, Path tmpFolder) {
        this.storage = storage;
        this.ffmpegPath = ffmpegPath;
        this.tmpFolder = tmpFolder;
    }

    @Override
    public void upload(User user, String name, InputStream stream) throws IOException, InterruptedException {
//        Path files_path = Paths.get(Paths.get("").toAbsolutePath().toString(), "src", "main", "files");
        System.out.println(tmpFolder);
        String[] resolutions = {"1080", "720", "360"};
        // Скачать файл, сохранить его локально
        Path local_file_path = Paths.get(tmpFolder.toString(), "output.mp4");
        Files.copy(stream, local_file_path); // TODO: путь надо получить в конструкторе
        // TODO: обычно в языках есть платформенно независимые решения для указания пути для файлов (Path, Paths)
        // Выполняешь несколько FFMPEG прочессов, которые делят на части полученное видео, используя разное качество

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
//                System.out.println(log);
            }
            int ret = process.waitFor();
//            System.out.printf( "Program exited with code: %d", ret);
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
//                System.out.println(log);
            }
            int ret2 = process2.waitFor();
            System.out.println(resolution + " cropped");
        }

//        pb.command(
//                ffmpegPath.toString(), "-i",  local_file_path.toString(),
//                "-force_key_frames", "\"expr:gte(t,n_forced*1)\"",
//                "-c:v",  "libx264", "-preset", "fast", "-c:a", "aac",
//                "-f", "segment", "-segment_time", "2", "-reset_timestamps", "1",
//                Paths.get(files_path.toString(),"output_%d.mp4").toString()
//        );
//        Process process = pb.redirectErrorStream(true).start();
//        InputStream ffmpeg_stream = process.getInputStream();
//        BufferedReader reader =  new BufferedReader(new InputStreamReader(ffmpeg_stream));
//
//        while (true) {
//            String log = reader.readLine();
//            if (Objects.isNull(log))
//                break;
//            System.out.println(log);
//        }
//        int ret = process.waitFor();
//        System.out.printf( "Program exited with code: %d", ret);

        // Длину видео можно узнать с помощью ffprobe, либо посмотреть, сколько файлов создалось
        File[] file_list = new File(tmpFolder.toString()).listFiles();
        int segment_count = (file_list.length - 1 - resolutions.length) / resolutions.length;
        System.out.println("\nsegment_count: " + segment_count);

        // Надо сохранить все сегменты в storage
        for (String resolution : resolutions) {
            for (int i = 0; i < segment_count; i++) {
                InputStream is = Files.newInputStream(Paths.get(tmpFolder.toString(), "output_" + resolution + "_" + i + ".mp4"));
                String segment_name = name + "_segment_" + resolution + "_" + Integer.toString(i);
                storage.upload(segment_name, is);
            }
        }

        // Надо сохранить в Базе данных информацию о том, сколько сегментов у одного видео есть
        map.put(name, segment_count);
        System.out.println(map);

        for(File file: file_list) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
            // для начала база данных - это Map<String, Integer>
//        youtube.upload(user, name, stream);
        }

    @Override
    public InputStream load (User user, String name, int startSegment, int resolution){
//        youtube.load(user, name, startSec);
        String file_name = name + "_segment_" + resolution + "_" + startSegment;
        System.out.println(file_name);
        return storage.download(file_name);
    }

    public static void main(String[] args) {
        try(final InputStream is = Files.newInputStream(Paths.get(Paths.get("").toAbsolutePath().toString(), "src", "main", "sample-15s.mp4"))) {
            SegmentatedYoutube sy = new SegmentatedYoutube(
                    new FakeStorage(),
                    Paths.get("C:\\Users\\programmer\\Downloads\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\ffmpeg-2025-07-17-git-bc8d06d541-full_build\\bin\\ffmpeg.exe"),
                    Paths.get(Paths.get("").toAbsolutePath().toString(), "src", "main", "files")
            );
            sy.upload(new FakeUser(), "name", is);
            sy.load(new FakeUser(), "name", 3, 720);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
