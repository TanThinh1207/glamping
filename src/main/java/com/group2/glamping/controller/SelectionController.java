package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.SelectionResponse;
import com.group2.glamping.service.interfaces.SelectionService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/selections")
@Tag(name = "Selection API", description = "API for managing selections in the Glamping system")
@RequiredArgsConstructor
public class SelectionController {

    private final SelectionService selectionService;
    private static final Logger logger = LoggerFactory.getLogger(SelectionController.class);

    // Create Selection
    @PostMapping()
    @Operation(
            summary = "Create a new selection",
            description = "Creates a new selection with the provided name, description, price, image and campsite ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selection created successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> createSelection(
            @Parameter(description = "Name of the selection", required = true)
            @RequestParam String name,
            @Parameter(description = "Description of the selection", required = true)
            @RequestParam String description,
            @Parameter(description = "Price of the selection", required = true)
            @RequestParam double price,
            @Parameter(description = "Campsite ID", required = true)
            @RequestParam Integer campSiteId
    ) {
        try {
            SelectionRequest request = new SelectionRequest(null, name, description, price, campSiteId);
            SelectionResponse response = selectionService.createSelection(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Selection created successfully", response));
        } catch (Exception e) {
            logger.error("Error while creating selection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Update Selection
    @PutMapping()
    @Operation(
            summary = "Update an existing selection",
            description = "Updates an existing selection with the provided ID, name, description, price, image and campsite ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selection updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Selection not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> updateSelection(
            @Parameter(description = "ID of the selection to update", required = true)
            @RequestParam Integer id,
            @Parameter(description = "Updated name of the selection", required = true)
            @RequestParam String name,
            @Parameter(description = "Updated description of the selection", required = true)
            @RequestParam String description,
            @Parameter(description = "Updated price of the selection", required = true)
            @RequestParam double price,
            @Parameter(description = "Updated campsite ID", required = true)
            @RequestParam Integer campSiteId
    ) {
        try {
            SelectionRequest request = new SelectionRequest(id, name, description, price, campSiteId);
            SelectionResponse response = selectionService.updateSelection(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Selection updated successfully", response));
        } catch (Exception e) {
            logger.error("Error while updating selection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // Get Selections
    @Operation(
            summary = "Get list of selections",
            description = "Retrieve a paginated list of selections with optional filtering and field selection",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selections retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @GetMapping
    public ResponseEntity<Object> getSelections(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(selectionService.getFilteredSelections(params, page, size, fields, sortBy, direction));
    }

    // Soft Delete Selection
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a selection",
            description = "Marks a selection as inactive without permanently removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selection soft-deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Selection not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> softDeleteSelection(
            @Parameter(description = "ID of the selection to delete", example = "3")
            @PathVariable int id
    ) {
        try {
            SelectionResponse response = selectionService.softDeleteSelection(id);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Selection soft-deleted successfully", response));
        } catch (Exception e) {
            logger.error("Error while soft deleting selection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

}
