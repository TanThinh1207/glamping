package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.service.interfaces.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@Tag(name = "Facility API", description = "API for managing facilities in the Glamping system")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;
    private static final Logger logger = LoggerFactory.getLogger(FacilityController.class);

    // Create Facility
    @PostMapping()
    @Operation(
            summary = "Create a new facility",
            description = "Creates a new facility with the provided name, description, and optional image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facility created successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> createFacility(
            @Parameter(description = "Name of the facility", required = true)
            @RequestParam String name,
            @Parameter(description = "Description of the facility", required = true)
            @RequestParam String description,
            @Parameter(description = "Image file for the facility (optional)")
            @RequestParam(required = false) MultipartFile image
    ) {
        try {
            FacilityRequest request = new FacilityRequest(null, name, description);
            FacilityResponse response = facilityService.createFacility(request, image);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facility created successfully", response));
        } catch (Exception e) {
            logger.error("Error while creating facility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Update Facility
    @PutMapping()
    @Operation(
            summary = "Update an existing facility",
            description = "Updates an existing facility with the provided id, name, description, and optional image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facility updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Facility not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> updateFacility(
            @Parameter(description = "ID of the facility to update", required = true)
            @RequestParam Integer id,
            @Parameter(description = "Updated name of the facility", required = true)
            @RequestParam String name,
            @Parameter(description = "Updated description of the facility", required = true)
            @RequestParam String description,
            @Parameter(description = "Updated image file for the facility (optional)")
            @RequestParam(required = false) MultipartFile image
    ) {
        try {
            FacilityRequest request = new FacilityRequest(id, name, description);
            FacilityResponse response = facilityService.updateFacility(request, image);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facility updated successfully", response));
        } catch (Exception e) {
            logger.error("Error while updating facility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve All Facilities
    @GetMapping()
    @Operation(
            summary = "Retrieve all facilities",
            description = "Retrieves all facilities.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facilities retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getAllFacilities() {
        try {
            List<FacilityResponse> facilities = facilityService.getAllFacilities();
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facilities retrieved successfully", facilities));
        } catch (Exception e) {
            logger.error("Error while retrieving all facilities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve Facilities by Name
    @GetMapping("/name/{name}")
    @Operation(
            summary = "Retrieve facilities by name",
            description = "Retrieves facilities filtered by the provided name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facilities retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getFacilitiesByName(
            @Parameter(description = "Name of the facility", example = "Gym", required = true)
            @PathVariable(required = false) String name
    ) {
        try {
            List<FacilityResponse> responses = (name == null || name.trim().isEmpty())
                    ? facilityService.getAllFacilities()
                    : facilityService.getFacilityByName(name);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facilities retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving facilities by name", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve Facilities by Status
    @GetMapping("/status/{status}")
    @Operation(
            summary = "Retrieve facilities by status",
            description = "Retrieves facilities filtered by status (true for active, false for inactive).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facilities retrieved successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getFacilitiesByStatus(
            @Parameter(description = "Status of the facility", example = "true", required = true)
            @PathVariable(required = false) Boolean status
    ) {
        try {
            List<FacilityResponse> facilities = facilityService.getFacilitiesByStatus(status);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facilities retrieved successfully", facilities));
        } catch (Exception e) {
            logger.error("Error while retrieving facilities by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Delete Facility (Soft Delete)
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a facility",
            description = "Marks a facility as deleted (inactive) instead of removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facility deleted successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> deleteFacility(
            @Parameter(description = "ID of the facility to delete", example = "3")
            @PathVariable Integer id
    ) {
        try {
            FacilityResponse response = facilityService.deleteFacility(id);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facility deleted successfully", response));
        } catch (Exception e) {
            logger.error("Error while deleting facility", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
}
