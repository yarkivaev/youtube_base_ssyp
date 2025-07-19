package ru.ssyp.youtube;

import io.minio.MinioClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class S3StorageTest {

    private static final MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    private Storage storage;

    @BeforeEach
    public void beforeEach() {
        storage = new S3Storage(minioClient, "test-bucket");
    }

    @Test
    public void testUploadDownload() throws IOException {
        storage.upload("new-test-file.txt", new ByteArrayInputStream("Hello world!!!!".getBytes()));
        InputStream fileContent = storage.download("new-test-file.txt");
        assertEquals("Hello world!!!!", new String(fileContent.readAllBytes()));
    }

    @AfterAll
    public static void afterAll() throws Exception {
        minioClient.close();
    }
}
