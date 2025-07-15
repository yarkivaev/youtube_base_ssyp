package ru.ssyp.youtube;

import java.io.File;

public interface Youtube {
    void upload(User user, String name, File file);

    File load(User user, String name);
}