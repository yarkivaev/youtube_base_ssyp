package ru.ssyp.youtube;

import java.io.File;
import java.nio.file.Paths;

public class FileStorage implements Storage {

    @Override
    public void upload(String name, File file) {
        File stored = Paths.get("data", name).toFile();
        file.renameTo(stored);
    }

    @Override
    public File download(String name) {
        return Paths.get("data", name).toFile();
    }
    
}