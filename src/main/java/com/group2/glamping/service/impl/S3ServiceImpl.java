package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.repository.*;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final CampSiteRepository campSiteRepository;
    private final SelectionRepository selectionRepository;
    private final UtilityRepository utilityRepository;
    private final FacilityRepository facilityRepository;
    private final CampTypeRepository campTypeRepository;
    private final StringRedisTemplate redisTemplate;


    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Override
    public List<String> uploadCampSiteFiles(List<MultipartFile> files, int id) {
        try {
            List<String> fileNames = new ArrayList<>();
            String fileName;
            String folderName = "CampSite";
            CampSite campsite = campSiteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));
            String prefix = campsite.getName() + "/CAMPSITE";
            List<Image> images = new ArrayList<>();
            for (MultipartFile file : files) {
                fileName = folderName + "/" + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(fileName)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromBytes(file.getBytes()));
                Image image = new Image();
                image.setPath(fileName);
                image.setCampSite(campsite);
                images.add(image);
                fileNames.add(fileName);
            }
            campsite.getImageList().addAll(images);
            campSiteRepository.save(campsite);
            return fileNames;
        } catch (IOException e) {
            throw new AppException(ErrorCode.S3_ERROR);
        }

    }

    @Override
    public String uploadFile(MultipartFile file, String fileType, int id) {
        try {
            String prefix;
            String folderName;
            String fileName = "";
            switch (fileType) {
                case "campSite" -> uploadCampSiteFiles(Collections.singletonList(file), id);
                case "selection" -> {
                    Selection selection = selectionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SELECTION_NOT_FOUND));
                    CampSite campSite = campSiteRepository.findById(selection.getCampSite().getId()).orElseThrow(
                            () -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));
                    folderName = "CampSite";
                    prefix = campSite.getName() + "/SELECTION_";
                    fileName = folderName + "/" + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    selection.setImageUrl(fileName);
                    selectionRepository.save(selection);
                }
                case "facility" -> {
                    folderName = "Facility";
                    prefix = "FACILITY_";
                    Facility facility = facilityRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.FACILITY_NOT_FOUND));
                    fileName = folderName + "/" + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    facility.setImageUrl(fileName);
                    facilityRepository.save(facility);
                }
                case "utility" -> {
                    folderName = "Utility";
                    prefix = "UTILITY_";
                    Utility utility = utilityRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.UTILITY_NOT_FOUND));
                    fileName = folderName + "/" + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    utility.setImageUrl(fileName);
                    utilityRepository.save(utility);
                }
                case "campType" -> {
                    folderName = "CampType";
                    prefix = "CAMPTYPE_";
                    CampType campType = campTypeRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.UTILITY_NOT_FOUND));
                    fileName = folderName + "/" + prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    campType.setImage(fileName);
                    campTypeRepository.save(campType);
                }
                default -> throw new AppException(ErrorCode.INVALID_REQUEST, "The provided fileType is not supported.");
            }
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            return fileName;
        } catch (AppException e) {
            throw new AppException(ErrorCode.S3_ERROR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String generatePresignedUrl(String fileName, int expirationInSeconds) {
        String cacheKey = "s3-url:" + fileName;
        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);

        if (cachedUrl != null) {
            System.out.println("Cache hit: " + cachedUrl);
            return cachedUrl;
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            return "No image";
        }

        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(expirationInSeconds))
                    .getObjectRequest(req -> req.bucket(bucketName).key(fileName))
                    .build();
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            redisTemplate.opsForValue().set(cacheKey, presignedUrl, Duration.ofSeconds(expirationInSeconds - 300));

            return presignedUrl;
        } catch (S3Exception e) {
            throw new AppException(ErrorCode.S3_ERROR);
        }
    }

    @Override
    public String getFileUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "No image";
        }
        return cloudFrontDomain + "/" + path;
    }

}
