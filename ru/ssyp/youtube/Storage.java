package ru.ssyp.youtube;

import java.io.File;

public interface Storage {
    void upload(String name, File file);

    void download(String name);
}
