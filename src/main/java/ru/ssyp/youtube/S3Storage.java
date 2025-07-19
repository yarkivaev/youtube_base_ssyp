package ru.ssyp.youtube;
import io.minio.*;
import io.minio.errors.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.InputStream;

public class S3Storage implements Storage {

    private final MinioClient minioClient;

    private final String bucketName;

    public S3Storage(MinioClient minioClient, String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    @Override
    public void upload(String name, InputStream inputStream) {
        ObjectWriteResponse objectWriteResponse = null;
        try {
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
        System.out.println("Файл успешно загружен" + objectWriteResponse.object());
    }

    @Override
    public InputStream download(String name) {
        try {
            Files.deleteIfExists(Paths.get(name));
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(name)
                            .filename(name)
                            .build());
            return new FileInputStream(name);
        }
            catch (
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

}






