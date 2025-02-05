package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.CampSiteCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
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

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCampType(@RequestBody CampTypeCreateRequest request) {
        Optional<CampTypeResponse> campType = campTypeService.saveCampType(request);

        Map<String, Object> response = new HashMap<>();

        if (campType.isPresent()) {
            response.put("statusCode", HttpStatus.OK.value());
            response.put("mess", "CampType created successfully");
            response.put("data", campType.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("statusCode", HttpStatus.BAD_REQUEST.value());
            response.put("mess", "Failed to create CampType");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/findCampType/{campSiteId}")
    public ResponseEntity<Map<String, Object>> getCampTypesByCampSite(@PathVariable int campSiteId) {
        List<CampType> campTypes = campTypeService.findByCampSiteId(campSiteId);

        Map<String, Object> response = new HashMap<>();
        if (!campTypes.isEmpty()) {
            response.put("statusCode", HttpStatus.OK.value());
            response.put("mess", "Retrieved CampTypes successfully");
            response.put("data", campTypes);
            return ResponseEntity.ok(response);
        } else {
            response.put("statusCode", HttpStatus.NOT_FOUND.value());
            response.put("mess", "No CampTypes found for this CampSite");
            response.put("data", Collections.emptyList());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/availableQuantity")
    public ResponseEntity<Long> getAvailableQuantity(
            @RequestParam Integer campTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime checkOut) {
        Long availableQuantity = campTypeService.findAvailableSlots(campTypeId, checkIn, checkOut);
        return new ResponseEntity<>(availableQuantity, HttpStatus.OK);
    }

    @DeleteMapping("/{campTypeId}")
    public ResponseEntity<Map<String, Object>> deleteCampType(@PathVariable int campTypeId) {
        Map<String, Object> response = campTypeService.softDeleteCampType(campTypeId);
        return ResponseEntity.status((int) response.get("statusCode")).body(response);
    }
}

