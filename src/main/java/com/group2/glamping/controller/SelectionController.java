package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.SelectionResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.service.interfaces.SelectionService;
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
@RequestMapping("/api/selections")
@RequiredArgsConstructor
public class SelectionController {

    private final SelectionService selectionService;
    private static final Logger logger = LoggerFactory.getLogger(SelectionController.class);

    //CREATE AND UPDATE
    @PostMapping("/create_update")
    public ResponseEntity<BaseResponse> createOrUpdateSelection(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam double price,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam Integer campSiteId
    ) {
        try {
            SelectionRequest request = new SelectionRequest(id, name, description, price, image, campSiteId);
            SelectionResponse response = selectionService.createOrUpdateSelection(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Selection saved successfully", response));
        } catch (Exception e) {
            logger.error("Error in create/update selection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred", null));
        }
    }

    // RETRIEVE
    @GetMapping("/retrieve")
    public ResponseEntity<BaseResponse> retrieveSelections(@RequestParam(required = false) String name) {
        try {
            List<SelectionResponse> responses;
            if (name == null || name.trim().isEmpty()) {
                responses = selectionService.getAllSelections();
            } else {
                responses = selectionService.getSelectionsByName(name);
            }
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Data retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error in retrieving selections", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred", null));
        }
    }

    // DELETE
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> softDeleteSelection(@RequestParam int id) {
        try {
            selectionService.softDeleteSelection(id);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Selection soft-deleted successfully", null));
        } catch (Exception e) {
            logger.error("Error in soft deleting selection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred", null));
        }
    }
}
