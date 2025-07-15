package ru.ssyp.youtube;

import java.io.File;

public interface Storage {
    void upload(String name, File file);

    File download(String name);
}
