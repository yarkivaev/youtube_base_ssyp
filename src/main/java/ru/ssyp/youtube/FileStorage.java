package ru.ssyp.youtube;

import java.io.*;
import java.util.Base64;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
    Abandon all hope ye, who import this package.
    It doesn't get better from this point.

    I tried to add some comments.
 */


/*
=== IMPLEMENTED FIXES:
- added one big try block for the whole function
- Added support for files up to 1 GB
- Removed hash verification (due to unability to generate it when the whole file is not stored in RAM at once)
- Created a constructor that allows to adjust the directory, max file size
- Added comments
*/

public class FileStorage implements Storage {
    // Declaring three parameters of a filestorage instance. Two constructors can be used:
    String dir = "C://SsypYoutubeBaisicStorage//";
    int MaxSize = 1073741824; // Maximum size of an accepted file
    //Creating a file storage and adjusting all the parameters
    public FileStorage(String directory, int MaxStringSize){
        dir = directory;
        MaxSize = MaxStringSize;
    }
    //Creating a file storage and leaving max size at its default value
    public FileStorage(String directory){ //Creating a file storage using the default MaxSize
        dir = directory;
    }
    //Upload function: loads the file INTO the storage, DOES NOT verify sha-256 hash
    @Override
    public void upload(String name, InputStream stream) throws FileNotFoundException {
        //The whole thing is enclosed in a big try that catches all the IOexeptions (there are going to be a lot of them)
        try {
            int length = stream.available();
            if (length > MaxSize || length < 1) { // 1 GB in bytes (because we will need to store ts in our RAM)
                throw new UnsupportedOperationException("Error: invalid file size");
            }
            int counter_global = 0; // Tracking the bytes already written to ensure we don't exceed max size
            // This is necessary as .available() doesn't always return the correct stream size
            int byteRead = stream.read(); // Reading the initial byte from input stream
            File newFile = new File(STR."\{dir}\{name}.txt");
            if (!newFile.createNewFile()) { // Ensuring a file has been created
                throw new RuntimeException("Error: unable to create a file");
            }
            FileOutputStream fos = new FileOutputStream(newFile); // Getting an output stream
            while(byteRead != -1) { // Doing this while input stream doesn't end
                counter_global += 1; // Incrementing the total byte tracker
                fos.write((byte) byteRead); // Putting the byte into the file
                byteRead = stream.read(); // Reading the next one
                if (counter_global > MaxSize){ // Ensuring we're still within the max size
                    throw new RuntimeException("Error: invalid file size");
                }
            }
            fos.close(); //Closing the stream and returning void
            return;
        } catch (IOException e) {
            System.out.println(STR."An error occurred: \{e.getMessage()}");
            throw new RuntimeException(e);
        }
    }
    // This is for downloading things
    @Override
    public InputStream download(String name) {
        // First, let's locate all files in our glorious directory:
        File startPath = new File(dir);
        File[] AllFiles = startPath.listFiles();
        File FoundFile = null; // Here we're going to keep the file that we were searching for
        //Make sure the list that we're about to go through is not empty:
        if(AllFiles == null){
            throw new UnsupportedOperationException("Error: file not found");
        }
        // Now check all the files in that list and break when we find the right one:
        for (File allFile : AllFiles) {
            String CurrentName = allFile.getName();
            CurrentName = CurrentName.substring(0, CurrentName.length() - 4);
            if (CurrentName.equals(name)) { // If the file is correct, save it and quit the loop
                FoundFile = allFile;
                break;
            }
        }
        // If we have found something, then return the stream from it and we're done
        if (FoundFile != null){
            try {
                return new FileInputStream(FoundFile);
            } catch (FileNotFoundException e) {
                throw new UnsupportedOperationException("Error: file not found");
            }
        }
        // If we're here, that means found file is null
        throw new UnsupportedOperationException("Error: file not found");
    }
}
