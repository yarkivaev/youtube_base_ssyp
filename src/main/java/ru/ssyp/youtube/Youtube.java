package ru.ssyp.youtube;

import java.io.File;
import java.io.InputStream;

public interface Youtube {
    void upload(User user, String title, String description, String name, InputStream stream);

    InputStream load(User user, String name);
}