package com.group2.glamping.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.interfaces.CampSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/campsites")
@Tag(name = "CampSite API", description = "API for managing Campsites")
@RequiredArgsConstructor
public class CampSiteController {

    private final CampSiteService campSiteService;

    private static final Logger logger = LoggerFactory.getLogger(CampSiteController.class);

    @Operation(
            summary = "Get list of campsites",
            description = """
                        Retrieve a paginated list of campsites with optional filtering, field selection, and sorting.
                        Filtering parameters available in the 'params' map:
                        - id: Exact match on campsite ID
                        - name: Partial match on campsite name
                        - status: Exact match on campsite status
                        - city: Partial match on city name
                        - address: Partial match on address
                        - placeTypeName: Comma-separated list of place type names
                        - utilityName: Comma-separated list of utility names
                        - userId: Exact match on the user ID associated with the campsite
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Campsites retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @GetMapping
    public ResponseEntity<Object> getCampSites(
            @Parameter(description = "Filtering parameters like id, name, city, status, placeTypeName, utilityName, and userId")
            @RequestParam Map<String, String> params,
            @Parameter(description = "Page number for pagination", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10")
            @Min(value = 2, message = "Page size must be greater than 1")
            int size,
            @Parameter(description = "Fields to include in the response, comma-separated", example = "id,name,city")
            @RequestParam(name = "fields", required = false) String fields,
            @Parameter(description = "Field to sort by", example = "name")
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction: ASC or DESC", example = "ASC")
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction
    ) throws JsonProcessingException {
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();
        System.out.println("API xử lý trong: " + (end - start) + "ms");
        return ResponseEntity.ok(campSiteService.getFilteredCampSites(params, page, size, fields, sortBy, direction));
    }


    @Operation(
            summary = "Create a new campsite",
            description = "Add a new campsite with selections and camp types, each supporting an image upload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Campsite created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @PostMapping
    public ResponseEntity<BaseResponse> createCampSite(@RequestBody CampSiteRequest campSiteRequest) {
        try {
//            logger.info("Parsed campSiteRequest: {}", campSiteRequest);
            return ResponseEntity.ok().body(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .data(campSiteService.saveCampSite(campSiteRequest)
                            .orElseThrow(() -> new IllegalArgumentException("Failed to save camp site")))
                    .message("Camp Site created successfully")
                    .build());

        } catch (AppException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .message(e.getMessage())
                    .data(null)
                    .statusCode(e.getErrorCode().getCode())
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
    @CacheEvict(value = "camps", key = "#id")
    @PatchMapping(value = "/{id}")
    public ResponseEntity<BaseResponse> updateCampSite(
            @PathVariable int id,
            @RequestBody CampSiteUpdateRequest updatedCampSite) throws JsonMappingException {
        return ResponseEntity.ok(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .data(campSiteService.updateCampSite(id, updatedCampSite))
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
