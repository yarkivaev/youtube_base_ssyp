package ru.ssyp.youtube;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.shadow.com.univocity.parsers.conversions.Conversions.string;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/*
* This file contains tests for FileStorage class
* The last commit passes all of them
*/

class FileStorageTest {

    // I mean, the names of the tests are pretty self-explanatory
    
    private FileStorage fileStorage;
    
    @BeforeEach
    void setUp() {
        String tmpDir = "C://SsypYoutubeBaisicStorage//";
        fileStorage = new FileStorage(tmpDir, 128); //Setting very low max file size for testing purposes
        File directory = new File(tmpDir);
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (!file.isDirectory()) {
                file.delete();
            }
        }
    }
    
    @Test
    void testUploadDownload() throws IOException {
        fileStorage.upload("FileRandom.bin", new ByteArrayInputStream( "Hello!".getBytes()));
        InputStream io = fileStorage.download("FileRandom.bin");
        String read = "Read: ";
        while(io.available() != -1){
            int attemptAtRead = io.read();
            if (attemptAtRead != -1) {
                read += (char) attemptAtRead;
                System.out.println(attemptAtRead + " ");
            }
            else{
                break;
            }
        }
        System.out.print(read);
        assertEquals("Read: Hello!", read);
    }
    
    @Test
    void testDownloadThrowsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> {
            fileStorage.download("testFile");
        });
    }
    
    @Test
    void testStorageImplementsInterface() {
        assertTrue(fileStorage instanceof Storage);
    }

    @Test
    void testMaxFileSize(){
        assertThrows(UnsupportedOperationException.class, () -> {
            fileStorage.upload("FileHeavy.bin", new ByteArrayInputStream( "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris efficitur aliquam leo, quis scelerisque enim semper in. Duis eleifend porta sapien sed sagittis. Quisque lacinia nisi sagittis neque vehicula, vitae molestie odio egestas. Sed pulvinar aenean".getBytes()));
        });
    }

    @Test
    void testNoInput(){
        assertThrows(UnsupportedOperationException.class, () -> {
            fileStorage.upload("FileEmpty.bin", new ByteArrayInputStream( "".getBytes()));
        });
    }

    @Test
    void testTwoIdenticalFiles(){
        assertThrows(RuntimeException.class, () -> {
            fileStorage.upload("File.bin", new ByteArrayInputStream("One content".getBytes()));
            fileStorage.upload("File.bin", new ByteArrayInputStream("Other content".getBytes()));
        });
    }

    @Test
    void testFindWithMultipleFiles() throws IOException {
        fileStorage.upload("FileA.bin", new ByteArrayInputStream( "Hello from 1!".getBytes()));
        fileStorage.upload("FileB.bin", new ByteArrayInputStream( "Hello from 2!".getBytes()));
        fileStorage.upload("FileC.bin", new ByteArrayInputStream( "Hello from 3!".getBytes()));
        fileStorage.upload("FileD.bin", new ByteArrayInputStream( "Hello from 4!".getBytes()));
        fileStorage.upload("FileE.bin", new ByteArrayInputStream( "Hello from 5!".getBytes()));
        fileStorage.upload("FileF.bin", new ByteArrayInputStream( "Hello from 6!".getBytes()));
        InputStream io = fileStorage.download("FileD.bin");
        String read = "Read: ";
        while(io.available() != -1){
            int attemptAtRead = io.read();
            if (attemptAtRead != -1) {
                read += (char) attemptAtRead;
            }
            else{
                break;
            }
        }
        System.out.print(read);
        assertEquals("Read: Hello from 4!", read);
    }
}
