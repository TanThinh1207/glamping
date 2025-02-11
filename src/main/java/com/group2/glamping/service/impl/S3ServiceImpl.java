package com.group2.glamping.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file, String folderName, String prefix) {
        try {
            String fileName = folderName + "/" + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

            return fileName;
        } catch (IOException e) {
            throw new AppException(ErrorCode.S3_ERROR);
        }
    }

    @Override
    public InputStreamResource downloadFile(String fileName) throws IOException {
        if (!amazonS3.doesObjectExist(bucketName, fileName)) {
            throw new RuntimeException("File not found: " + fileName);
        }
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);
        InputStream inputStream = s3Object.getObjectContent();
        return new InputStreamResource(inputStream);
    }

    @Override
    public String deleteFile(String fileName) {
        if (!amazonS3.doesObjectExist(bucketName, fileName)) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        amazonS3.deleteObject(bucketName, fileName);
        return fileName + " removed successfully.";
    }

    @Override
    public List<String> listFiles(String folderName) {
        ObjectListing objectListing = amazonS3.listObjects(bucketName, folderName + "/");
        return objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }


    @Override
    public String generatePresignedUrl(String fileName) {

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1 giờ
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName)
                        .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    @Override
    public List<String> generatePresignedUrls(List<String> fileKeys) {
        List<String> urls = new ArrayList<>();
        for (String fileKey : fileKeys) {
            Date expiration = new Date();
            expiration.setTime(expiration.getTime() + 1000 * 60 * 60);  // 1 giờ hết hạn

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, fileKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);

            urls.add(amazonS3.generatePresignedUrl(request).toString());
        }
        return urls;
    }

}
