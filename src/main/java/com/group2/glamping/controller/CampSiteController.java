package com.group2.glamping.controller;

import com.group2.glamping.model.dto.requests.CampSiteCreateRequest;
import com.group2.glamping.model.dto.requests.CampSiteUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
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
    public ResponseEntity<BaseResponse> getAllCampSites() {
        List<CampSiteResponse> campsites = campSiteService.getCampSites();

        return ResponseEntity.ok(
                new BaseResponse(HttpStatus.OK.value(), "Camp sites retrieved successfully", campsites)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getCampSiteById(@PathVariable int id) {
        Optional<CampSiteResponse> campsite = campSiteService.getCampSiteBasicDetail(id);
        return campsite.map(site -> ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site found", site)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Camp site not found", null)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse> createCampSite(@RequestBody CampSiteCreateRequest request) {
        Optional<CampSite> campSite = campSiteService.saveCampSite(request);
        return campSite.map(site -> ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse(HttpStatus.CREATED.value(), "Camp site created successfully", site))).orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Failed to create camp site", null)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateCampSite(@PathVariable int id, @RequestBody CampSiteUpdateRequest updatedCampSite) {
        Optional<CampSite> existingCampSite = campSiteService.findCampSiteById(id);
        if (existingCampSite.isPresent()) {
            CampSite campSite = existingCampSite.get();
            campSite.setName(updatedCampSite.getName());
            campSite.setAddress(updatedCampSite.getAddress());
            campSite.setLatitude(updatedCampSite.getLatitude());
            campSite.setLongitude(updatedCampSite.getLongitude());
            campSiteService.updateCampSite(campSite);

            Optional<CampSiteResponse> updatedResponse = campSiteService.getCampSiteBasicDetail(id);
            return updatedResponse.map(response -> ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site updated successfully", response)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Camp site not found after update", null)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Camp site not found", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteCampSite(@PathVariable int id) {
        Optional<CampSite> campsite = campSiteService.findCampSiteById(id);
        if (campsite.isPresent()) {
            CampSite campSite = campsite.get();
            campSite.setStatus(CampSiteStatus.Not_Available);
            campSiteService.updateCampSite(campSite);
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site deleted successfully", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Camp site not found", null));
    }
}
