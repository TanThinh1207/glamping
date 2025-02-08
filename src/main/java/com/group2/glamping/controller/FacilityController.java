package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.service.interfaces.FacilityService;
import com.group2.glamping.service.interfaces.UtilityService;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping("/save")
    public ResponseEntity<FacilityResponse> createOrUpdateFacility(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile image) {
        return ResponseEntity.ok(facilityService.createOrUpdateFacility( new FacilityRequest(id, name, description, image)));
    }

    @GetMapping
    public ResponseEntity<?> getFacilities(@RequestParam(value = "name", required = false) String name) {
        if (name != null) {
            return ResponseEntity.ok(facilityService.getFacilityByName(name));
        }
        return ResponseEntity.ok(facilityService.getAllFacilities());
    }


    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Integer id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }
}
