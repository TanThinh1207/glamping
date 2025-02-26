package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
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
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
            @RequestParam String name
    ) {
        try {
            PlaceTypeRequest request = PlaceTypeRequest.builder()
                    .id(null)
                    .name(name)
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
            @Valid @RequestBody PlaceTypeRequest request

    ) {
        try {
            PlaceTypeResponse response = placeTypeService.updatePlaceType(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Place type updated successfully", response));
        } catch (AppException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .statusCode(e.getErrorCode().getCode())
                    .data(null)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            logger.error("Error while updating place type: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Retrieve PlaceTypes
    @Operation(
            summary = "Get list of place types",
            description = "Retrieve a paginated list of place types with optional filtering and field selection",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place types retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @GetMapping
    public ResponseEntity<MappingJacksonValue> getPlaceTypes(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields) {
        return ResponseEntity.ok(placeTypeService.getFilteredPlaceTypes(params, page, size, fields));
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
