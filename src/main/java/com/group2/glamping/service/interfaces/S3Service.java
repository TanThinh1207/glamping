package com.group2.glamping.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {

    String uploadCampSiteFiles(List<MultipartFile> files, int id);

    String uploadFile(MultipartFile file, String fileType, int campSiteId);

    String generatePresignedUrl(String fileName);

}