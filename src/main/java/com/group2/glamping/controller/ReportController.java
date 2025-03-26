package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.ReportRequest;
import com.group2.glamping.model.dto.requests.ReportUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.ReportResponse;
import com.group2.glamping.service.interfaces.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report API", description = "API for managing reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    // <editor-fold default state="collapsed" desc="Create a new report">
    @PostMapping
    @Operation(
            summary = "Create a new report",
            description = "Creates a new report with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report created successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                        {
                                            "statusCode": 200,
                                            "message": "Report created successfully",
                                            "data": {
                                                "id": 6,
                                                "campSite": {
                                                    "id": 1,
                                                    "name": "Sa Đéc Glamping",
                                                    "address": "353 ĐT848, Tân Khánh Đông, Sa Đéc, Đồng Tháp 81000"
                                                },
                                                "user": {
                                                    "id": 2,
                                                    "email": "manager@example.com",
                                                    "firstname": "John",
                                                    "lastname": "Doe"
                                                },
                                                "status": "Pending",
                                                "createdTime": "2025-03-26 16:57:01",
                                                "updatedTime": "2025-03-26 18:00:00",
                                                "message": "The WC is dirty but has been cleaned",
                                                "reportType": "Facility Issue"
                                            }
                                        }
                                        """))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "statusCode": 500,
                                              "message": "An unexpected error occurred.",
                                              "data": null
                                            }
                                            """))),
            }
    )
    public ResponseEntity<BaseResponse> createReport(@Valid @RequestBody ReportRequest request) {
        try {
            ReportResponse response = reportService.createReport(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Report created successfully", response));
        } catch (Exception e) {
            logger.error("Error while creating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.", null));
        }
    }
    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Update an existing report">
    @PutMapping
    @Operation(
            summary = "Update an existing report",
            description = "Updates a report with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report updated successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                        {
                                            "statusCode": 200,
                                            "message": "Report updated successfully",
                                            "data": {
                                                "id": 6,
                                                "campSite": {
                                                    "id": 1,
                                                    "name": "Sa Đéc Glamping",
                                                    "address": "353 ĐT848, Tân Khánh Đông, Sa Đéc, Đồng Tháp 81000"
                                                },
                                                "user": {
                                                    "id": 2,
                                                    "email": "manager@example.com",
                                                    "firstname": "John",
                                                    "lastname": "Nguyen"
                                                },
                                                "status": "Pending",
                                                "createdTime": "2025-03-26 16:57:01",
                                                "updatedTime": "2025-03-26 18:00:00",
                                                "message": "The WC is dirty but has been cleaned",
                                                "reportType": "Facility Issue"
                                            }
                                        }
                                        """))),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> updateReport(@Valid @RequestBody ReportUpdateRequest request) {
        try {
            ReportResponse response = reportService.updateReport(request);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Report updated successfully", response));
        } catch (AppException e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(new BaseResponse(e.getErrorCode().getCode(), e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error while updating report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.", null));
        }
    }
    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Get filtered Report">
    @GetMapping
    @Operation(
            summary = "Get list of reports",
            description = "Retrieve a paginated list of reports with optional filtering, sorting, and field selection.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reports retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                        {
                                          "statusCode": 200,
                                          "message": "Return using dynamic filter successfully",
                                          "data": {
                                            "content": [
                                              {
                                                "id": 1,
                                                "campSite": {
                                                  "id": 1,
                                                  "name": "Sa Đéc Glamping",
                                                  "address": "353 ĐT848, Tân Khánh Đông, Sa Đéc, Đồng Tháp 81000"
                                                },
                                                "user": {
                                                  "id": 3,
                                                  "email": "user@example.com",
                                                  "firstname": "John",
                                                  "lastname": "Doe"
                                                },
                                                "status": "Resolved",
                                                "createdTime": "2025-03-26 16:17:41",
                                                "message": "Eco-cabin was wonderful but the WiFi didn't work well.",
                                                "reportType": "Suggestion"
                                              },
                                              {
                                                "id": 2,
                                                "campSite": {
                                                  "id": 2,
                                                  "name": "Panorama Glamping",
                                                  "address": "ngay cây xăng, Ấp Mít Nài, Phú Quý, Định Quán, Đồng Nai 700000"
                                                },
                                                "user": {
                                                  "id": 2,
                                                  "email": "manager@example.com",
                                                  "firstname": "Manager",
                                                  "lastname": "User"
                                                },
                                                "status": "Resolved",
                                                "createdTime": "2025-03-26 16:17:41",
                                                "message": "Great camping experience. Would return again!",
                                                "reportType": "Complaint"
                                              },
                                              {
                                                "id": 6,
                                                "campSite": {
                                                  "id": 1,
                                                  "name": "Sa Đéc Glamping",
                                                  "address": "353 ĐT848, Tân Khánh Đông, Sa Đéc, Đồng Tháp 81000"
                                                },
                                                "user": {
                                                  "id": 2,
                                                  "email": "manager@example.com",
                                                  "firstname": "Manager",
                                                  "lastname": "User"
                                                },
                                                "status": "Pending",
                                                "createdTime": "2025-03-26 16:57:01",
                                                "message": "The WC is dirty",
                                                "reportType": "Facility Issue"
                                              }
                                            ],
                                            "totalRecords": 2,
                                            "totalPages": 1,
                                            "currentPage": 0,
                                            "currentPageSize": 2
                                          }
                                        }
                                        """))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                        {
                                          "statusCode": 400,
                                          "message": "Invalid request parameters",
                                          "data": null
                                        }
                                        """)))
            }
    )
    public ResponseEntity<Object> getFilteredReports(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(reportService.getFilteredReports(params, page, size, fields, sortBy, direction));
    }

    // </editor-fold>

    // <editor-fold default state="collapsed" desc="Soft delete a report">
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a report",
            description = "Marks a report as deleted instead of removing it permanently.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Report deleted successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "statusCode": 200,
                                              "message": "Report deleted successfully"
                                            }
                                            """))),
                    @ApiResponse(responseCode = "404", description = "Report not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> deleteReport(
            @Parameter(description = "ID of the report to delete") @PathVariable Integer id) {
        try {
            ReportResponse response = reportService.softDeleteReport(id);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Report deleted successfully", response));
        } catch (Exception e) {
            logger.error("Error while deleting report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.", null));
        }
    }
    // </editor-fold>
}
