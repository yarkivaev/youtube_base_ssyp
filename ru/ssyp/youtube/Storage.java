package ru.ssyp.youtube;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.net.*;

public interface Storage {
    void upload(String name, File file);

    File download(String name) throws IOException;
}
