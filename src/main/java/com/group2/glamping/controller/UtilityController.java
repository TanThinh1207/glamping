package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.service.interfaces.UtilityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/utilities")
@RequiredArgsConstructor
public class UtilityController {

    private final UtilityService utilityService;
    private static final Logger logger = LoggerFactory.getLogger(UtilityController.class); // Sửa lỗi này

    //Create and update
    @PostMapping("/save_update")
    public ResponseEntity<BaseResponse> saveOrUpdate(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile image) {
        try {
            UtilityRequest request = new UtilityRequest(id, name, image);
            UtilityResponse response = utilityService.saveOrUpdateUtility(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Data saved successfully", response));
        } catch (Exception e) {
            logger.error("Error while saving/updating utility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    //Retrieve
    @GetMapping("/retrieve")
    public ResponseEntity<BaseResponse> retrieveUtilities(@RequestParam(required = false) String name) {
        try {
            List<UtilityResponse> responses;
            //Name is null -> get all
            if (name == null || name.trim().isEmpty()) {
                responses = utilityService.getAllUtilities();
            } else { //get Utility by name
                responses = utilityService.getUtilitiesByName(name);
            }
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Data retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving utilities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    //Delete
    @PostMapping("/delete")
    public ResponseEntity<BaseResponse> softDeleteUtility(@RequestParam int id) {
        try {
            utilityService.softDeleteUtility(id);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Utility deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error while soft deleting utility", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
}
