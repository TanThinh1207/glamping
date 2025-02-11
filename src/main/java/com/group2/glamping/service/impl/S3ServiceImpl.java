package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folderName, String prefix) {
        try {
            String fileName = folderName + "/" + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));

            return fileName;
        } catch (IOException e) {
            throw new AppException(ErrorCode.S3_ERROR);
        }
    }

    @Override
    public InputStreamResource downloadFile(String fileName) {
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build());

        return new InputStreamResource(s3Object);
    }


    @Override
    public String deleteFile(String fileName) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());
            return fileName + " removed successfully.";
        } catch (S3Exception e) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Override
    public List<String> listFiles(String folderName) {
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderName + "/")
                .build());

        return listResponse.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    @Override
    public String generatePresignedUrl(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty or null");
        }

        try { 
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1)) // URL hết hạn sau 1 giờ
                    .getObjectRequest(req -> req.bucket(bucketName).key(fileName))
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (S3Exception e) {
            throw new AppException(ErrorCode.S3_ERROR);
        }
    }


    @Override
    public List<String> generatePresignedUrls(List<String> fileKeys) {
        return fileKeys.stream()
                .map(this::generatePresignedUrl)
                .collect(Collectors.toList());
    }
}
