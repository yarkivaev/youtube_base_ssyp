package ru.ssyp.youtube;

import io.minio.*;
import io.minio.errors.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class S3Storage implements Storage {
    private final MinioClient minioClient;
    private final String bucketName;
    private final Path downloadDestination;

    public S3Storage(MinioClient minioClient, String bucketName, Path downloadDestination) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.downloadDestination = downloadDestination;
    }

    private static void createBucketIfNotExists(MinioClient minioClient)
            throws ErrorResponseException, InsufficientDataException, InternalException,
            InvalidKeyException, InvalidResponseException, IOException,
            NoSuchAlgorithmException, ServerException, XmlParserException {

        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket("test-bucket")
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket("test-bucket")
                    .build());
            System.out.println("Bucket '" + "test-bucket" + "' created successfully.");
        }
    }

    @Override
    public void upload(String name, InputStream inputStream) {
        ObjectWriteResponse objectWriteResponse;

        try {
            createBucketIfNotExists(minioClient);
            objectWriteResponse = minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(name)
                    .stream(inputStream, -1, 1024 * 1024 * 5)
                    .build());
        } catch (
                ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Файл успешно загружен: " + objectWriteResponse.object());
    }

    @Override
    public InputStream download(String name) {
        try {
            createBucketIfNotExists(minioClient);
            Path downloadPath = Paths.get(downloadDestination.toString(), name);
            Files.deleteIfExists(downloadPath);
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(name)
                            .filename(downloadPath.toString())
                            .build());
            return new FileInputStream(downloadPath.toString());
        } catch (
                ErrorResponseException
                | InsufficientDataException
                | InternalException
                | InvalidKeyException
                | InvalidResponseException
                | IOException
                | NoSuchAlgorithmException
                | ServerException
                | XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
}
