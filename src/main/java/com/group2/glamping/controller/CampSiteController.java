package com.group2.glamping.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
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
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/campsites")
@Tag(name = "CampSite API", description = "API for managing Campsites")
@RequiredArgsConstructor
public class CampSiteController {

    private final CampSiteService campSiteService;
    private final ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(CampSiteController.class);

    @Operation(
            summary = "Get list of campsites",
            description = "Retrieve a paginated list of campsites with optional filtering and field selection",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Campsites retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @GetMapping
    public ResponseEntity<MappingJacksonValue> getCampSites(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields) {
        return ResponseEntity.ok(campSiteService.getFilteredCampSites(params, page, size, fields));
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
    public ResponseEntity<BaseResponse> createCampSite(@RequestBody String campSiteRequest) {
        try {
            CampSiteRequest request = objectMapper.readValue(campSiteRequest, CampSiteRequest.class);

            logger.info("Parsed campSiteRequest: {}", request);
            return ResponseEntity.ok().body(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .data(campSiteService.saveCampSite(request))
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


    @Operation(
            summary = "Update a campsite",
            description = "Modify details of an existing campsite by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Camp site not found")
            }
    )
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> updateCampSite(
            @PathVariable int id,
            @RequestPart("updatedCampSite") String updatedCampSiteJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        System.out.println("Received updatedCampSite JSON: " + updatedCampSiteJson);
        System.out.println("Received files: " + (files != null ? files.size() : 0));

        CampSiteUpdateRequest updatedCampSite;
        try {
            updatedCampSite = new ObjectMapper().readValue(updatedCampSiteJson, CampSiteUpdateRequest.class);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Invalid JSON format for updatedCampSite");
        }

        campSiteService.updateCampSite(id, updatedCampSite, files);
        return ResponseEntity.ok(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("CampSite updated successfully")
                .build());
    }


    //DELETE
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


}
