package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.CampSiteCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.service.impl.CampTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/camp_types")
public class CampTypeController {

    private final CampTypeServiceImpl campTypeService;


    @GetMapping("/availableQuantity")
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
        Optional<CampTypeResponse> campType = campTypeService.saveCampType(request);

        BaseResponse response = new BaseResponse();

        if (campType.isPresent()) {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("CampType created successfully");
            response.setData(campType.get());
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Failed to create CampType");
            response.setData(null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update/{campTypeId}")
    public ResponseEntity<BaseResponse> updateCampType(
            @PathVariable int campTypeId,
            @RequestBody CampTypeUpdateRequest request) {

        Optional<CampTypeResponse> updatedCampType = campTypeService.updateCampType(campTypeId, request);

        BaseResponse response = new BaseResponse();

        if (updatedCampType.isPresent()) {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("CampType updated successfully");
            response.setData(updatedCampType.get());
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("CampType not found");
            response.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //RETRIEVE
    @GetMapping("/findCampType/{campSiteId}")
    public ResponseEntity<BaseResponse> getCampTypesByCampSite(@PathVariable int campSiteId) {
        List<CampTypeResponse> campTypes = campTypeService.findByCampSiteId(campSiteId);

        BaseResponse response = new BaseResponse();
        if (!campTypes.isEmpty()) {
            response.setStatusCode(HttpStatus.OK.value());
            response.setMessage("Retrieved CampTypes successfully");
            response.setData(campTypes);
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("No CampTypes found for this CampSite");
            response.setData(Collections.emptyList());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //DELETE
    @DeleteMapping("/delete/{campTypeId}")
    public ResponseEntity<BaseResponse> deleteCampType(@PathVariable int campTypeId) {
        BaseResponse response = campTypeService.softDeleteCampType(campTypeId);
        return ResponseEntity.status((int) response.getStatusCode()).body(response);
    }
}

