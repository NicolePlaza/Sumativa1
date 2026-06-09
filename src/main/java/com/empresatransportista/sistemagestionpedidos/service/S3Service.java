package com.empresatransportista.sistemagestionpedidos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String upload(Path filePath, String key) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
        s3Client.putObject(request, filePath);
        return key;
    }

    public byte[] download(String key) {
        return s3Client.getObjectAsBytes(
            GetObjectRequest.builder().bucket(bucket).key(key).build()
        ).asByteArray();
    }

    public void delete(String key) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}