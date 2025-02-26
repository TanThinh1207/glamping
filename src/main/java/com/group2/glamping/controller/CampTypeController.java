package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.impl.CampTypeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/camp-types")
public class CampTypeController {

    private final CampTypeServiceImpl campTypeService;

    @GetMapping("/available-quantity")
    public ResponseEntity<Long> getAvailableQuantity(
            @RequestParam Integer campTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {
        Long availableQuantity = campTypeService.findAvailableSlots(campTypeId, checkIn, checkOut);
        return new ResponseEntity<>(availableQuantity, HttpStatus.OK);
    }


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
    public ResponseEntity<MappingJacksonValue> getCampTypes(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields) {
        return ResponseEntity.ok(campTypeService.getFilteredCampTypes(params, page, size, fields));
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
        return ResponseEntity.status((int) response.getStatusCode()).body(response);
    }
}

