package ru.ssyp.youtube;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
    Abandon all hope ye, who import this package.
 */

public class FileStorage implements Storage {
    // Declaring three parameters of a file storage instance. Two constructors can be used:
    final private Path dir;
    final private int chunkSize;
    public FileStorage(Path dir, int chunkSize){
        this.dir = dir;
        this.chunkSize = chunkSize;
    }
    public FileStorage(Path dir){
        this(dir, 32768);
    }
    public FileStorage(){
        this(makeDefaultDir());
    }
    private static Path makeDefaultDir(){
        File dire = new File("SsypYoutubeBasicStorage");
        if (!dire.exists()){
            if (!dire.mkdir()) {
                throw new RuntimeException("Failed to create a directory");
            } else {
                System.out.println("Created.");
            }
        }
        else{
            if(!dire.isDirectory()){
                throw new RuntimeException("Path exists and is not a directory");
            }
        }
        return FileSystems.getDefault().getPath("SsypYoutubeBasicStorage");
    }
    //Upload function: loads the file INTO the storage, DOES NOT verify sha-256 hash
    @Override
    public void upload(String name, InputStream stream) {
        //The whole thing is enclosed in a big try that catches all the IOexeptions (there are going to be a lot of them)
        try {
            File newFile = new File(dir.toString(), name + ".txt");
            if (!newFile.createNewFile()) { throw new RuntimeException("Unable to create a file"); }
            FileOutputStream fos = new FileOutputStream(newFile);

            // Read chunks from the stream and put them in the file:
            byte[] buf = new byte[chunkSize];
            int len;
            while ((len = stream.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            // Close the stream
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(String name) throws IOException {
        Files.delete(Paths.get(dir.toString(), name + ".txt"));
    }

    // This is for downloading things:
    @Override
    public InputStream download(String name) {
        File TargetFile = new File(dir.toString(), name + ".txt");
        try {
            return new FileInputStream(TargetFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
    }
}
