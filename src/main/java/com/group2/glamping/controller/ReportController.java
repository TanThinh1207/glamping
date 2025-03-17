package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.ReportRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.ReportResponse;
import com.group2.glamping.service.interfaces.ReportService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report API", description = "API for managing reports in the Glamping system")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    // <editor-fold defaultstate="collapsed" desc="Create Report">
    @PostMapping()
    @Operation(
            summary = "Create a new report",
            description = "Creates a new report for a campsite, associated with a user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            name = "Example Request",
                            value = "{ \"campSiteId\": 2, \"userId\": 5, \"status\": \"PENDING\", \"message\": \"Broken equipment\", \"reportType\": \"DAMAGED_FACILITY\" }"
                    ))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Report created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(value = "{ \"statusCode\": 201, \"message\": \"Report created successfully\", \"data\": { \"id\": 1, \"campSiteId\": 2, \"userId\": 5, \"status\": \"PENDING\", \"createdTime\": \"2025-03-14T12:00:00\", \"message\": \"Broken equipment\", \"reportType\": \"DAMAGED_FACILITY\" }}"))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data",
                            content = @Content(examples = @ExampleObject(value = "{ \"statusCode\": 400, \"message\": \"Invalid request data\", \"data\": null }")))
            }
    )
    public ResponseEntity<BaseResponse> createReport(@RequestBody ReportRequest request) {
        ReportResponse response = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(HttpStatus.CREATED.value(), "Report created successfully", response));
    }

    // </editor-fold>

    // Get All Reports
    @GetMapping()
    @Operation(
            summary = "Get all reports",
            description = "Retrieve all reports in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
            }
    )
    public ResponseEntity<BaseResponse> getAllReports() {
        List<ReportResponse> reports = reportService.getAllReports();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Reports retrieved successfully", reports));
    }

    // <editor-fold defaultstate="collapsed" desc="Update Report">
    @PutMapping("/{id}")
    @Operation(
            summary = "Update a report",
            description = "Updates the details of an existing report.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Report not found")
            }
    )
    public ResponseEntity<BaseResponse> updateReport(@PathVariable int id, @RequestBody ReportRequest request) {
        ReportResponse response = reportService.updateReport(id, request);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Report updated successfully", response));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Delete Report (Soft Delete)">
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a report",
            description = "Deletes a report by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report deleted successfully",
                            content = @Content(examples = @ExampleObject(value = "{ \"statusCode\": 200, \"message\": \"Report deleted successfully\", \"data\": null }"))),
                    @ApiResponse(responseCode = "404", description = "Report not found",
                            content = @Content(examples = @ExampleObject(value = "{ \"statusCode\": 404, \"message\": \"Report not found\", \"data\": null }")))
            }
    )
    public ResponseEntity<BaseResponse> deleteReport(@PathVariable int id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Report deleted successfully", null));
    }
    // </editor-fold>
}
