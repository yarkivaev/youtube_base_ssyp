package ru.ssyp.youtube;

import java.io.File;
import java.io.FileNotFoundException;

public interface Storage {
    void upload(String name, File file) throws FileNotFoundException;

    File download(String name);
}
