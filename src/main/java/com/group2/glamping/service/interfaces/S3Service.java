package com.group2.glamping.service.interfaces;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {

    String uploadFile(MultipartFile file, String folderName, String prefix);

    InputStreamResource downloadFile(String fileName) throws IOException;

    String deleteFile(String fileName);

    List<String> listFiles(String folderName);

    String generatePresignedUrl(String fileName);

    List<String> generatePresignedUrls(List<String> fileKeys);
}