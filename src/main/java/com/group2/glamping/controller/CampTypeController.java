package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.impl.CampTypeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/camp-types")
public class CampTypeController {

    private final CampTypeServiceImpl campTypeService;

    //CREATE
    @PostMapping
    public ResponseEntity<BaseResponse> createCampType(@RequestBody CampTypeCreateRequest request) {
        BaseResponse response = campTypeService.saveCampType(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    //Retrieve Camp Type
    @Operation(
            summary = "Get list of camp types",
            description = "Retrieve a paginated list of camp types with optional filtering and field selection",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp types retrieved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @GetMapping
    public ResponseEntity<Object> getCampTypes(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {
        return ResponseEntity.ok(campTypeService.getFilteredCampTypes(params, page, size, fields, sortBy, direction));
    }

    //UPDATE
    @PutMapping("/{campTypeId}")
    public ResponseEntity<BaseResponse> updateCampType(
            @PathVariable int campTypeId,
            @RequestBody CampTypeUpdateRequest request) {

        BaseResponse response = campTypeService.updateCampType(campTypeId, request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    //DELETE
    @DeleteMapping("/{campTypeId}")
    public ResponseEntity<BaseResponse> deleteCampType(@PathVariable int campTypeId) {
        BaseResponse response = campTypeService.softDeleteCampType(campTypeId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/{campTypeId}/facilities/")
    @Operation(
            summary = "Update facilities of a camp type",
            description = "Update the list of facilities associated with a camp type based on the given campTypeId.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Facilities updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data"),
                    @ApiResponse(responseCode = "404", description = "Camp type not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<BaseResponse> updateFacilities(
            @PathVariable
            @Parameter(description = "ID of the camp type to update", example = "1") int campTypeId,

            @RequestBody
            @Schema(description = "List of facility IDs to associate with the camp type", example = "[101, 102, 103]")
            List<Integer> facilities
    ) {
        return ResponseEntity.ok(BaseResponse.builder()
                .data(campTypeService.updateFacility(campTypeId, facilities))
                .message("Facilities updated successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }


}

