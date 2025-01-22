package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.CampSiteCreateRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.service.interfaces.CampSiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campsites")
@RequiredArgsConstructor
public class CampSiteController {

    private final CampSiteService campSiteService;

    @GetMapping
    public ResponseEntity<List<CampSiteResponse>> getAllCampSites() {
        List<CampSiteResponse> campsites = campSiteService.getCampSites();
        return ResponseEntity.ok(campsites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampSite> getCampSiteById(@PathVariable int id) {
        Optional<CampSite> campsite = campSiteService.findCampSiteById(id);
        return campsite.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createCampSite(@RequestBody CampSiteCreateRequest request) {
        Optional<CampSite> campSite = campSiteService.saveCampSite(request);
        if (campSite.isPresent()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<CampSiteResponse> updateCampSite(@PathVariable int id, @RequestBody CampSiteUpdateRequest updatedCampSite) {
        Optional<CampSite> existingCampSite = campSiteService.findCampSiteById(id);
        if (existingCampSite.isPresent()) {
            existingCampSite.get().setName(updatedCampSite.getName());
            existingCampSite.get().setAddress(updatedCampSite.getAddress());
            existingCampSite.get().setLatitude(updatedCampSite.getLatitude());
            existingCampSite.get().setLongitude(updatedCampSite.getLongitude());
            campSiteService.updateCampSite(existingCampSite.get());

        }
        if (campSiteService.getCampSiteBasicDetail(id).isPresent()) {
            return ResponseEntity.ok(campSiteService.getCampSiteBasicDetail(id).get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampSite(@PathVariable int id) {
        Optional<CampSite> campsite = campSiteService.findCampSiteById(id);
        if (campsite.isPresent()) {
            CampSite campSite = campsite.get();
            campSite.setStatus(CampSiteStatus.Not_Available);
            campSiteService.updateCampSite(campSite);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
