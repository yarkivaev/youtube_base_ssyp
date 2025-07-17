package ru.ssyp.youtube;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SegmentatedYoutube implements Youtube {

    Map<String, Integer> map = new HashMap<>();

    private final Storage storage;

    public SegmentatedYoutube(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void upload(User user, String name, InputStream stream) throws IOException, InterruptedException {
        Path files_path = Paths.get(Paths.get("").toAbsolutePath().toString(), "src", "main", "files");
        System.out.println(files_path);
        // Скачать файл, сохранить его локально
        Path local_file_path = Paths.get(files_path.toString(),"output.mp4");
        Files.copy(stream, local_file_path); // TODO: путь надо получить в конструкторе
        // TODO: обычно в языках есть платформенно независимые решения для указания пути для файлов (Path, Paths)
        // Выполняешь несколько FFMPEG прочессов, которые делят на части полученное видео, используя разное качество
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(
                "ffmpeg", "-i",  local_file_path.toString(),
                "-force_key_frames", "\"expr:gte(t,n_forced*1)\"",
                "-c:v",  "libx264", "-preset", "fast", "-c:a", "aac",
                "-f", "segment", "-segment_time", "2", "-reset_timestamps", "1",
                Paths.get(files_path.toString(),"output_%d.mp4").toString()
        );
        Process process = pb.redirectErrorStream(true).start();
        InputStream ffmpeg_stream = process.getInputStream();
        BufferedReader reader =  new BufferedReader(new InputStreamReader(ffmpeg_stream));

        while (true) {
            String log = reader.readLine();
            if (Objects.isNull(log))
                break;
            System.out.println(log);
        }
        int ret = process.waitFor();
        System.out.printf("Program exited with code: %d", ret);
        // Длину видео можно узнать с помощью ffprobe, либо посмотреть, сколько файлов создалось
        File[] file_list = new File(files_path.toString()).listFiles();
        int segment_count = file_list.length - 1;
        // Надо сохранить все сегменты в storage
        for (int i = 0; i < segment_count; i++){
            InputStream is = Files.newInputStream(Paths.get(files_path.toString(), "output_" + i + ".mp4"));
            String segment_name = name + "_segment_" + Integer.toString(i);
            storage.upload(segment_name, is);
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
    public InputStream load(User user, String name, Double startSec) {
//        youtube.load(user, name, startSec);
        return null;
    }

    public static void main(String[] args) {
        try(final InputStream is = Files.newInputStream(Paths.get(Paths.get("").toAbsolutePath().toString(), "src", "main", "sample-15s.mp4"))) {
            SegmentatedYoutube sy = new SegmentatedYoutube(new FakeStorage());
            sy.upload(new FakeUser(), "name", is);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
