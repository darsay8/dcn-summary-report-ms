package dev.rm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.folder-name}")
    private String folderName;

    @Value("${aws.region}")
    private String region;

    private final S3Client s3Client;

    public void uploadReport(String fileName, InputStream inputStream) throws IOException {

        log.info("Uploading report to S3 bucket {}...", bucketName + "/" + folderName + "/" + fileName);

        String key = folderName + "/" + fileName;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/json")
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, inputStream.available()));
    }
}
