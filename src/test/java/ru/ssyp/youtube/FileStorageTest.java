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
*/

class FileStorageTest {
    
    private FileStorage fileStorage;
    
    @BeforeEach
    void setUp() {
        fileStorage = new FileStorage();
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
                System.out.println(STR."\{attemptAtRead} ");
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
        assertThrows(RuntimeException.class, () -> {
            fileStorage.download("testFile");
        });
    }
    
    @Test
    void testStorageImplementsInterface() {
        assertTrue(fileStorage instanceof Storage);
    }

    @Test
    void testTwoIdenticalFiles() throws IOException {
        try {
            fileStorage.upload("File.bin", new ByteArrayInputStream("One content".getBytes()));
            fileStorage.upload("File.bin", new ByteArrayInputStream("Other content".getBytes()));
        } catch (RuntimeException | FileNotFoundException e) {
            System.out.print("Reached the exception clause, checking contence...");
            InputStream io = fileStorage.download("File.bin");
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
            assertEquals("Read: One content", read);

        }
    }

    @Test
    void testLargeFile() throws IOException {
        File TargetFile = new File("C:\\SsypYoutubeBaisicStorage\\100MB.txt");
        InputStream io;
        try {
            io = new FileInputStream(TargetFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
        fileStorage.upload("FileVeryHeavy.txt", io);
        io = fileStorage.download("FileVeryHeavy.txt");
        String read = "Read: ";
        int count = 0;
        while(count < 5){
            count += 1;
            int attemptAtRead = io.read();
            if (attemptAtRead != -1) {
                read += (char) attemptAtRead;
                System.out.println(STR."\{attemptAtRead} ");
            }
            else{
                break;
            }
        }
        System.out.print(read);
        assertEquals("Read: 20850", read);
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
