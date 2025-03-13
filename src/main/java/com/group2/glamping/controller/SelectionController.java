package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.SelectionResponse;
import com.group2.glamping.service.interfaces.SelectionService;
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
@RequestMapping("/api/selections")
@Tag(name = "Selection API", description = "API for managing selections in the Glamping system")
@RequiredArgsConstructor
public class SelectionController {

    private final SelectionService selectionService;
    private static final Logger logger = LoggerFactory.getLogger(SelectionController.class);

    // <editor-fold desc="Create Selection">
    @PostMapping()
    @Operation(
            summary = "Create a new selection",
            description = "Creates a new selection with the provided name, description, price, and campsite ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Example Request",
                            value = "{ \"name\": \"Luxury Tent\", \"description\": \"A beautiful tent with ocean view\", \"price\": 150.0, \"campSiteId\": 1 }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selection created successfully",
                            content = @Content(examples = @ExampleObject(
                                    value = "{ \"statusCode\": 200, \"message\": \"Selection created successfully\", \"data\": { \"id\": 1, \"name\": \"Luxury Tent\", \"description\": \"A beautiful tent with ocean view\", \"price\": 150.0, \"campSiteId\": 1 } }"
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 500,\n" +
                                            "  \"message\": \"An unexpected error occurred. Please try again later.\"" +
                                            "}")))
            }
    )
    public ResponseEntity<BaseResponse> createSelection(@RequestBody SelectionRequest request) {
        try {
            SelectionResponse response = selectionService.createSelection(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Selection created successfully", response));
        } catch (Exception e) {
            logger.error("Error while creating selection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Update Selection">
    @PutMapping()
    @Operation(
            summary = "Update an existing selection",
            description = "Updates an existing selection with the provided ID, name, description, price, and campsite ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Selection update request payload",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Example Input",
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "Luxury Tent",
                                                "description": "A luxurious tent with a sea view.",
                                                "price": 120.50,
                                                "campSiteId": 3
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Selection updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Example Response",
                                            value = """
                                                    {
                                                        "statusCode": 200,
                                                        "message": "Selection updated successfully",
                                                        "data": {
                                                            "id": 1,
                                                            "name": "Luxury Tent",
                                                            "description": "A luxurious tent with a sea view.",
                                                            "price": 120.50,
                                                            "campSiteId": 3
                                                        }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Place type not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 404,\n" +
                                            "  \"message\": \"Place type not found\",\n" +
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
    public ResponseEntity<BaseResponse> updateSelection(
            @RequestBody SelectionRequest request
    ) {
        try {
            SelectionResponse response = selectionService.updateSelection(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Selection updated successfully", response));
        } catch (Exception e) {
            logger.error("Error while updating selection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later.", null));
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Get Selections">
    @GetMapping
    @Operation(
            summary = "Get list of selections",
            description = "Retrieve a paginated list of selections with optional filtering and field selection",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selections retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    public ResponseEntity<Object> getSelections(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(selectionService.getFilteredSelections(params, page, size, fields, sortBy, direction));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Soft Delete Selection">
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a selection",
            description = "Marks a selection as inactive without permanently removing it from the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Selections deleted successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "statusCode": 200,
                                              "message": "Selections deleted successfully"
                                            }
                                            """))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\n" +
                                            "  \"statusCode\": 500,\n" +
                                            "  \"message\": \"An unexpected error occurred. Please try again later.\"" +
                                            "}")))
            }
    )
    public ResponseEntity<BaseResponse> softDeleteSelection(
            @Parameter(description = "Id of the selection", example = "2", required = true)
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
    // </editor-fold>

}
