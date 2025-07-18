package ru.ssyp.youtube;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
    Abandon all hope ye, who import this package.

    I tried to add some comments.
 */

public class FileStorage implements Storage {
    // Declaring three parameters of a file storage instance. Two constructors can be used:
    final Path dir;
    int chunkSize; // Size of a chunk in which the files are being uploaded
    //Creating a file storage and adjusting all the parameters
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
        File dire = new File("SsypYoutubeBaisicStorage");
        if (!dire.exists()){
            if (!dire.mkdir()) {
                throw new RuntimeException("Error: failed to create a directory");
            } else {
                System.out.println("Created.");
            }
        }
        else{
            if(!dire.isDirectory()){
                throw new RuntimeException("Error: path exists and is not a directory");
            }
        }

        return FileSystems.getDefault().getPath("SsypYoutubeBaisicStorage");
    }
    //Upload function: loads the file INTO the storage, DOES NOT verify sha-256 hash
    @Override
    public void upload(String name, InputStream stream) throws FileNotFoundException {
        //The whole thing is enclosed in a big try that catches all the IOexeptions (there are going to be a lot of them)
        try {
            File newFile = new File(dir.toString(),name + ".txt");
            if (!newFile.createNewFile()) { throw new RuntimeException("Error: unable to create a file"); }
            FileOutputStream fos = new FileOutputStream(newFile); // Getting an output stream

            // Now this is where things get though:
            byte[] buf = new byte[chunkSize];           // This is going to be buffer
            int len;                                    // So, in this var we store how many bytes we have read
            while ((len = stream.read(buf)) != -1) {    // Here, we simultaneously read things into buf and this function returns how many bytes have been read
                fos.write(buf, 0, len);             // And we copy this many bytes to the output with no offset
            }                                           // And we should be good
            fos.close();                                //Closing the stream and returning void

            return;
        } catch (IOException e) {
            System.out.println(STR."An error occurred: \{e.getMessage()}");
            throw new RuntimeException(e);
        }
    }
    // This is for downloading things
    @Override
    public InputStream download(String name) {
        File TargetFile = new File(dir.toString(), STR."\{name}.txt");
        try {
            return new FileInputStream(TargetFile);
        } catch (FileNotFoundException e) {
            throw new UnsupportedOperationException("Error: file not found");
        }
    }
}
