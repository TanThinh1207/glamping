package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.FacilityRepository;
import com.group2.glamping.repository.SelectionRepository;
import com.group2.glamping.repository.UtilityRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final CampSiteRepository campSiteRepository;
    private final SelectionRepository selectionRepository;
    private final UtilityRepository utilityRepository;
    private final FacilityRepository facilityRepository;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Override
    public String uploadCampSiteFiles(List<MultipartFile> files, int id) {
        try {
            String fileName = "";
            String folderName = "CampSite";
            CampSite campsite = campSiteRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));
            String prefix = campsite.getName() + "_CAMPSITE_";
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
            }
            campsite.getImageList().addAll(images);
            campSiteRepository.save(campsite);
            return fileName;
        } catch (IOException e) {
            throw new AppException(ErrorCode.S3_ERROR);
        }

    }

    @Override
    public String uploadFile(MultipartFile file, String fileType, int id) {
        try {
            String prefix = "";
            String folderName = "Others";
            String fileName = "";
            switch (fileType) {
                case "campSite" -> {
                    uploadCampSiteFiles(Collections.singletonList(file), id);
                }
                case "selection" -> {
                    Selection selection = selectionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SELECTION_NOT_FOUND));
                    folderName = "CampSite";
                    prefix = selection.getName() + "_SELECTION_";
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
