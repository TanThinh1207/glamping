package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.PlaceTypeRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;
import com.group2.glamping.service.interfaces.PlaceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/place-types")
@Tag(name = "PlaceType API", description = "API for managing place types")
@RequiredArgsConstructor
public class PlaceTypeController {

    private final PlaceTypeService placeTypeService;
    private static final Logger logger = LoggerFactory.getLogger(PlaceTypeController.class);

    // Create PlaceType
    @PostMapping()
    @Operation(
            summary = "Create a new place type",
            description = "Creates a new place type with the provided name and optional image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place type created successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> createPlaceType(
            @Parameter(description = "Name of the place type", required = true)
            @RequestParam String name,
            @Parameter(description = "Image file for the place type (optional)")
            @RequestParam(required = false) MultipartFile image
    ) {
        try {
            PlaceTypeRequest request = PlaceTypeRequest.builder()
                    .id(null)
                    .name(name)
//                    .imagePath(image)
                    .build();
            PlaceTypeResponse response = placeTypeService.createPlaceType(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Place type created successfully", response));
        } catch (Exception e) {
            logger.error("Error while creating place type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Update PlaceType
    @PutMapping()
    @Operation(
            summary = "Update an existing place type",
            description = "Updates an existing place type with the provided id, name, and optional image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place type updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Place type not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> updatePlaceType(
            @Parameter(description = "ID of the place type to update", required = true)
            @Valid @RequestBody PlaceTypeRequest request,
            @Parameter(description = "Updated image file for the place type (optional)")
            @RequestParam(required = false) MultipartFile image

    ) {
        try {
            PlaceTypeResponse response = placeTypeService.updatePlaceType(request, image);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Place type updated successfully", response));
        } catch (Exception e) {
            logger.error("Error while updating place type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve All PlaceTypes
    @GetMapping()
    @Operation(
            summary = "Retrieve all place types",
            description = "Retrieves all place types.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place types retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "No place types found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getAllPlaceTypes() {
        try {
            List<PlaceTypeResponse> responses = placeTypeService.getAllPlaceTypes();

            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "No place types found", responses));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Place types retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving all place types: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve PlaceTypes by Name
    @GetMapping("/name/{name}")
    @Operation(
            summary = "Retrieve place types by name",
            description = "Retrieves place types filtered by the provided name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place types retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "No place types found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getPlaceTypesByName(
            @Parameter(description = "Name of the place type", example = "Hotel", required = true)
            @PathVariable(required = false) String name
    ) {
        try {
            List<PlaceTypeResponse> responses = (name == null || name.trim().isEmpty())
                    ? placeTypeService.getAllPlaceTypes()
                    : placeTypeService.getPlaceTypeByName(name);

            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "No place types found", responses));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Place types retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving place types by name: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve PlaceTypes by Status
    @GetMapping("/status/{status}")
    @Operation(
            summary = "Retrieve place types by status",
            description = "Retrieves place types filtered by status (true for active, false for inactive).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place types retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "No place types found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> getPlaceTypesByStatus(
            @Parameter(description = "Status of the place type", example = "true", required = true)
            @PathVariable(required = true) Boolean status
    ) {
        try {
            List<PlaceTypeResponse> responses = placeTypeService.getPlaceTypeByStatus(status);

            if (responses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "No place types found with status: " + status, responses));
            }

            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Place types retrieved successfully", responses));
        } catch (Exception e) {
            logger.error("Error while retrieving place types by status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }


    // Soft Delete PlaceType
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a place type",
            description = "Marks a place type as deleted (inactive) instead of removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place type deleted successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> deletePlaceType(
            @Parameter(description = "ID of the place type to delete", example = "3")
            @PathVariable Integer id
    ) {
        try {
            PlaceTypeResponse response = placeTypeService.deletePlaceType(id);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Place type deleted successfully", response));
        } catch (Exception e) {
            logger.error("Error while deleting place type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
}
