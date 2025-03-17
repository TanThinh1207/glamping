package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.service.interfaces.UtilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/api/utilities")
@Tag(name = "Utility API", description = "API for managing utilities in the Glamping system")
@RequiredArgsConstructor
public class UtilityController {

    private final UtilityService utilityService;
    private static final Logger logger = LoggerFactory.getLogger(UtilityController.class);

    // <editor-fold default state="collapsed" desc="Create Utility">
    @PostMapping()
    @Operation(
            summary = "Create a new utility",
            description = "Creates a new utility with the provided name and optional image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Utility created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 200,\n" +
                                            "  \"message\": \"Utility created successfully\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 4,\n" +
                                            "    \"name\": \"Swimming Pool\",\n" +
                                            "    \"imagePath\": \"\",\n" +
                                            "    \"status\": true\n" +
                                            "  }\n" +
                                            "}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 500,\n" +
                                            "  \"message\": \"An unexpected error occurred. Please try again later.\",\n" +
                                            "  \"data\": null\n" +
                                            "}")))
            }
    )
    public ResponseEntity<BaseResponse> createUtility(
            @Parameter(description = "Name of the utility", example = "Swimming Pool", required = true)
            @RequestParam String name) {
        try {
            UtilityRequest request = new UtilityRequest(null, name);
            UtilityResponse response = utilityService.createUtility(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Utility created successfully", response));
        } catch (Exception e) {
            logger.error("Error while creating utility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
    // </editor-fold>

    // <editor-fold desc="Update Utility">
    @PutMapping
    @Operation(
            summary = "Update an existing utility",
            description = "Updates an existing utility with the provided ID, name, and optional image.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Utility updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 200,\n" +
                                            "  \"message\": \"Utility updated successfully\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"name\": \"Updated Swimming Pool\",\n" +
                                            "    \"imagePath\": \"\",\n" +
                                            "    \"status\": true\n" +
                                            "  }\n" +
                                            "}"))),
                    @ApiResponse(responseCode = "404", description = "Utility not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 404,\n" +
                                            "  \"message\": \"Utility not found\",\n" +
                                            "  \"data\": null\n" +
                                            "}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 500,\n" +
                                            "  \"message\": \"An unexpected error occurred. Please try again later.\",\n" +
                                            "  \"data\": null\n" +
                                            "}")))
            }
    )
    public ResponseEntity<BaseResponse> updateUtility(
            @Parameter(description = "ID of the utility to update", example = "1", required = true)
            @RequestParam Integer id,
            @Parameter(description = "New name of the utility", example = "Updated Swimming Pool")
            @RequestParam(required = false) String name) {
        try {
            UtilityRequest request = new UtilityRequest(id, name);
            UtilityResponse response = utilityService.updateUtility(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Utility updated successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error while updating utility: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Retrieve Utilities">
    @Operation(
            summary = "Get list of utilities",
            description = "Retrieve a paginated list of utilities with optional filtering, sorting, and field selection.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Utilities retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 200,\n" +
                                            "  \"message\": \"Utilities retrieved successfully\",\n" +
                                            "  \"data\": [\n" +
                                            "    {\n" +
                                            "      \"id\": 1,\n" +
                                            "      \"name\": \"Swimming Pool\",\n" +
                                            "      \"imagePath\": \"\",\n" +
                                            "      \"status\": true\n" +
                                            "    },\n" +
                                            "    {\n" +
                                            "      \"id\": 2,\n" +
                                            "      \"name\": \"BBQ Area\",\n" +
                                            "      \"imagePath\": \"\",\n" +
                                            "      \"status\": true\n" +
                                            "    }\n" +
                                            "  ]\n" +
                                            "}"))),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 400,\n" +
                                            "  \"message\": \"Invalid pagination parameters\",\n" +
                                            "  \"data\": null\n" +
                                            "}")))
            }
    )
    @GetMapping
    public ResponseEntity<Object> getUtilities(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(utilityService.getFilteredUtilities(params, page, size, fields, sortBy, direction));
    }
    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Delete Utility (Soft Delete)">
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a utility",
            description = "Marks a utility as deleted instead of removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Utility deleted successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 200,\n" +
                                            "  \"message\": \"Utility deleted successfully\"" +
                                            "}"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 500,\n" +
                                            "  \"message\": \"An unexpected error occurred. Please try again later.\"" +
                                            "}")))
            }
    )
    public ResponseEntity<BaseResponse> softDeleteUtility(
            @Parameter(description = "ID of the utility to delete", example = "3")
            @PathVariable int id) {
        try {
            UtilityResponse response = utilityService.softDeleteUtility(id);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Utility deleted successfully", response));
        } catch (Exception e) {
            logger.error("Error while soft deleting utility", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
    // </editor-fold>
}
