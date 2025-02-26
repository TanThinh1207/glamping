package com.group2.glamping.controller;

import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.interfaces.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3UploadController {

    private final S3Service s3Service;

    @Operation(summary = "Upload an image")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> upload(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Type of image (camp, avatar, etc.)", required = true)
            @RequestParam("type") String imageType,

            @Parameter(description = "Optional campSiteId if related to a campsite")
            @RequestParam(value = "id", required = false) Integer id) {
        String uploadImageMessage = String.format("Upload image for %s successful", imageType);
        return ResponseEntity.ok(BaseResponse.builder()
                .statusCode(HttpStatus.SC_OK)
                .message(uploadImageMessage)
                .data(s3Service.uploadFile(file, imageType, id))
                .build());
    }

    @Operation(summary = "Upload list of image for campsite")
    @PostMapping(value = "/campsite/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> uploadCampSiteImage(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") List<MultipartFile> file,

            @Parameter(description = "Optional campSiteId if related to a campsite")
            @RequestParam(value = "id", required = false) Integer id) {
        return ResponseEntity.ok(BaseResponse.builder()
                .statusCode(HttpStatus.SC_OK)
                .message("Upload image for campsite successful")
                .data(s3Service.uploadCampSiteFiles(file, id))
                .build());
    }

}
