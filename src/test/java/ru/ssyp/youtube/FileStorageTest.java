package ru.ssyp.youtube;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Path;

class FileStorageTest {
    
    private FileStorage fileStorage;
    
    @BeforeEach
    void setUp() {
        fileStorage = new FileStorage();
    }
    
//    @Test
//    void testUploadThrowsUnsupportedOperationException() {
//        File testFile = new File("test.txt");
//
//        assertThrows(UnsupportedOperationException.class, () -> {
//            fileStorage.upload("testFile", testFile);
//        });
//    }
//
//    @Test
//    void testDownloadThrowsUnsupportedOperationException() {
//        assertThrows(UnsupportedOperationException.class, () -> {
//            fileStorage.download("testFile");
//        });
//    }
//
//    @Test
//    void testStorageImplementsInterface() {
//        assertTrue(fileStorage instanceof Storage);
//    }
}