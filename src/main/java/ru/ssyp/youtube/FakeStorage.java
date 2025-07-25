package ru.ssyp.youtube;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FakeStorage implements Storage{

    record SavedVideo(String name, int fileSize) { }

    public final Map<String, SavedVideo> savedVideos;

    public FakeStorage(Map<String, SavedVideo> savedVideos) {
        this.savedVideos = savedVideos;
    }


    @Override
    public void upload(String name, InputStream inputStream) throws IOException {
        int fileSize = 0;
        int curBufSize;
        while ((curBufSize = inputStream.read(new byte[4096])) != -1) {
            fileSize += curBufSize;
        }
        savedVideos.put(name, new SavedVideo(name, fileSize));
    }

    @Override
    public void remove(String name) {

    }

    @Override
    public InputStream download(String name) {
        return new ByteArrayInputStream(
                name.getBytes()
        );
    }
}
