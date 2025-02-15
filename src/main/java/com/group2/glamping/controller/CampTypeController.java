package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.impl.CampTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


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

    //RETRIEVE
    @GetMapping("/{campSiteId}")
    public ResponseEntity<BaseResponse> getCampTypesByCampSite(@PathVariable int campSiteId) {
        BaseResponse response = campTypeService.findByCampSiteId(campSiteId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
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

