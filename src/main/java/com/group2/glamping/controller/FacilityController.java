package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.service.interfaces.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/facilities")
@Tag(name = "Facility API", description = "API for managing facilities in the Glamping system")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;
    private static final Logger logger = LoggerFactory.getLogger(FacilityController.class);

    // <editor-fold default state="collapsed" desc="Create Facility">
    @PostMapping()
    @Operation(
            summary = "Create a new facility",
            description = "Creates a new facility with the provided name and description.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facility created successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "statusCode": 200,
                                              "message": "Facility created successfully",
                                              "data": {
                                                "id": 5,
                                                "name": "Lounge Area",
                                                "description": "Relaxing lounge space with comfortable seating.",
                                                "status": true
                                              }
                                            }"""))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> createFacility(
            @Parameter(description = "Name of the facility", required = true)
            @RequestParam String name,
            @Parameter(description = "Description of the facility", required = true)
            @RequestParam String description
    ) {
        try {
            FacilityRequest request = new FacilityRequest(null, name, description);
            FacilityResponse response = facilityService.createFacility(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facility created successfully", response));
        } catch (Exception e) {
            logger.error("Error while creating facility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Update Facility">
    @PutMapping()
    @Operation(
            summary = "Update an existing facility",
            description = """
                     Updates an existing facility based on the provided ID.\s
                     Allows modifying the name and description of the facility.
                    \s""",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Facility update request",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example Update Facility Request",
                                    value = "{ \"id\": 1, \"name\": \"Luxury Pool\", \"description\": \"A high-end swimming pool with modern facilities.\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facility updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or missing parameters"),
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
            @RequestParam String description
    ) {
        try {
            FacilityRequest request = new FacilityRequest(id, name, description);
            FacilityResponse response = facilityService.updateFacility(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Facility updated successfully", response));
        } catch (Exception e) {
            logger.error("Error while updating facility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Get List of Facilities">
    @Operation(
            summary = "Get list of facilities",
            description = "Retrieve a paginated list of facilities with optional filtering, sorting, and field selection.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facilities retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "statusCode": 200,
                                              "message": "Facilities retrieved successfully",
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "name": "Swimming Pool",
                                                  "description": "Large outdoor pool with heating.",
                                                  "status": true
                                                },
                                                {
                                                  "id": 2,
                                                  "name": "BBQ Area",
                                                  "description": "Outdoor BBQ space with seating.",
                                                  "status": true
                                                }
                                              ]
                                            }"""))),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @GetMapping
    public ResponseEntity<Object> getFacilities(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(facilityService.getFilteredFacilities(params, page, size, fields, sortBy, direction));
    }
    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Delete Facility">
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a facility",
            description = "Marks a facility as deleted (inactive) instead of removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facility deleted successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "statusCode": 200,
                                              "message": "Facility deleted successfully",
                                              "data": {
                                                "id": 3,
                                                "name": "Lounge Area",
                                                "description": "Relaxing lounge space with comfortable seating.",
                                                "status": false
                                              }
                                            }"""))),
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
    // </editor-fold>
}
