package ru.ssyp.youtube;

import io.minio.MinioClient;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class S3StorageTest {

    private static final MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    private Storage storage;

    @BeforeEach
    public void beforeEach() throws IOException {
        storage = new S3Storage(minioClient, "test-bucket", Files.createTempDirectory("s3_tests"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"50mb.jpg", "100-mbjpg.jpg"})
    public void testUploadDownload(String mediaFile) throws IOException {
        Path path = Paths.get("src", "test", "resources", mediaFile);
        storage.upload(mediaFile, new FileInputStream(path.toFile()));
        InputStream file = new FileInputStream(path.toFile());
        InputStream fileContent = storage.download(mediaFile);
        assertEquals(streamSize(file), streamSize(fileContent));
        file.close();
        fileContent.close();
    }

    private int streamSize(InputStream stream) throws IOException {
        int size = 0;
        int curBuf;
        while ((curBuf = stream.read(new byte[4096])) != -1) {
            size += curBuf;
        }
        return size;
    }


    @AfterAll
    public static void afterAll() throws Exception {
        minioClient.close();
    }
}
