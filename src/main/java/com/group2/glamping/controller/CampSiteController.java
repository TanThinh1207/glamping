package com.group2.glamping.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.service.interfaces.CampSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campsites")
@Tag(name = "CampSite API", description = "API for managing Campsites")
@RequiredArgsConstructor
public class CampSiteController {

    private final CampSiteService campSiteService;

    private static final Logger logger = LoggerFactory.getLogger(CampSiteController.class);

    @Operation(
            summary = "Get all available campsites",
            description = "Retrieve a list of available campsites",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp sites retrieved successfully")
            }
    )
    @GetMapping
    public ResponseEntity<BaseResponse> getAllAvailableCampSites() {
        List<CampSiteResponse> campsites = campSiteService.getAvailableCampSites();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp sites retrieved successfully", campsites));
    }

    @Operation(
            summary = "Get all pending campsites",
            description = "Retrieve a list of campsites that are pending approval",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pending camp sites retrieved successfully")
            }
    )
    @GetMapping("/pending")
    public ResponseEntity<BaseResponse> getAllPendingCampSites() {
        List<CampSiteResponse> campsites = campSiteService.getPendingCampSites();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp sites retrieved successfully", campsites));
    }

    @Operation(
            summary = "Get campsite by ID",
            description = "Retrieve details of a specific campsite by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site found"),
                    @ApiResponse(responseCode = "404", description = "Camp site not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getCampSiteById(
            @Parameter(description = "ID of the campsite", example = "1") @PathVariable int id) {
        Optional<CampSiteResponse> campsite = campSiteService.getCampSiteBasicDetail(id);
        return campsite.map(site -> ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site found", site)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Camp site not found", null)));
    }

    @Operation(
            summary = "Create a new campsite",
            description = "Add a new campsite with selections and camp types, each supporting an image upload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Campsite created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> createCampSite(
            @RequestParam("campSiteRequest") String campSiteRequest,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "selectionFile", required = false) MultipartFile selectionFile,
            @RequestParam(value = "campTypeFile", required = false) MultipartFile campTypeFile
    ) {
        try {
            logger.info("Files: {}", files);
            if (files != null) {
                for (MultipartFile file : files) {
                    logger.info("Received file: {} (Size: {} bytes)", file.getOriginalFilename(), file.getSize());
                }
            }
            logger.info("Selection File: {} (Size: {})",
                    selectionFile != null ? selectionFile.getOriginalFilename() : "null",
                    selectionFile != null ? selectionFile.getSize() : "N/A");
            logger.info("Camp Type File: {} (Size: {})",
                    campTypeFile != null ? campTypeFile.getOriginalFilename() : "null",
                    campTypeFile != null ? campTypeFile.getSize() : "N/A");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            CampSiteRequest request = objectMapper.readValue(campSiteRequest, CampSiteRequest.class);
            logger.info("Parsed campSiteRequest: {}", request);
            logger.info("Files count: {}", (files != null ? files.size() : "null"));
            logger.info("Selection File: {}", (selectionFile != null ? selectionFile.getOriginalFilename() : "null"));
            logger.info("Camp Type File: {}", (campTypeFile != null ? campTypeFile.getOriginalFilename() : "null"));

            return ResponseEntity.ok().body(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .data(campSiteService.saveCampSite(request, files, selectionFile, campTypeFile))
                    .message("Camp Site created successfully")
                    .build());

        } catch (AppException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .message(e.getMessage())
                    .data(null)
                    .statusCode(e.getErrorCode().getCode())
                    .build());
        } catch (JsonProcessingException e) {
            logger.error("Error parsing campSiteRequest JSON: {}", campSiteRequest, e);
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .message("Invalid JSON format")
                    .data(null)
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .build());

        } catch (Exception e) {
            logger.error("Unexpected error while creating campsite", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponse.builder()
                    .message("Internal server error")
                    .data(null)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build());
        }
    }


//    @Operation(
//            summary = "Update a campsite",
//            description = "Modify details of an existing campsite by ID",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Camp site updated successfully"),
//                    @ApiResponse(responseCode = "404", description = "Camp site not found")
//            }
//    )
//    @PutMapping("/{id}")
//    public ResponseEntity<BaseResponse> updateCampSite(
//            @Parameter(description = "ID of the campsite", example = "1") @PathVariable int id,
//            @Valid @RequestBody CampSiteRequest updatedCampSite) {
//        try {
//            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
//                    "Camp site updated successfully",
//                    campSiteService.updateCampSite(id, updatedCampSite)));
//        } catch (AppException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
//                    "Camp site not found",
//                    null));
//        }
//    }

    @Operation(
            summary = "Enable a campsite",
            description = "Set status of a campsite from NOT AVAILABLE to AVAILABLE by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site enabled successfully"),
                    @ApiResponse(responseCode = "404", description = "Camp site not found")
            }
    )
    @PutMapping("/enableCampSite/{id}")
    public ResponseEntity<BaseResponse> enableCampSite(
            @Parameter(description = "ID of the campsite", example = "1") @PathVariable int id) {
        try {
            Optional<CampSiteResponse> response = campSiteService.enableCampSite(id);
            return response.map(campSiteResponse -> ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                            "Camp site enabled successfully", campSiteResponse)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
                            "No Camp Site found with ID: " + id + " has status Not Available.",
                            null)));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
                    "Camp site not found",
                    null));
        }
    }

    @Operation(
            summary = "Delete a campsite",
            description = "Remove a campsite by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Camp site not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteCampSite(
            @Parameter(description = "ID of the campsite", example = "1") @PathVariable int id) {
        campSiteService.deleteCampSite(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site deleted successfully", null));
    }

    @Operation(
            summary = "Search campsites by name or city",
            description = "Filter campsites based on name or city",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned successfully")
            }
    )
    @GetMapping("/filterbynameorcity/{filterCondition}")
    public ResponseEntity<BaseResponse> searchCampSiteByNameOrCity(
            @Parameter(description = "Search keyword (camp name or city)", example = "Hanoi") @PathVariable String
                    filterCondition) {
        BaseResponse response = campSiteService.searchCampSiteByNameOrCity(filterCondition);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
