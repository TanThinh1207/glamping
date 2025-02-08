package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
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
    public ResponseEntity<BaseResponse> createCampSite(@RequestBody CampSiteRequest request) {
        try {
            return ResponseEntity.ok().body(BaseResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .data(campSiteService.saveCampSite(request))
                    .message("Camp Site created successfully")
                    .build());
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(BaseResponse.builder()
                    .message(e.getMessage())
                    .data(null)
                    .statusCode(e.getErrorCode().getCode())
                    .build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateCampSite(@PathVariable int id, @RequestBody CampSiteRequest updatedCampSite) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse(HttpStatus.OK.value(), "Camp site updated successfully", campSiteService.updateCampSite(id, updatedCampSite)));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Camp site not found", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteCampSite(@PathVariable int id) {

//        CampSite campSite = campsite.get();
//        campSite.setStatus(CampSiteStatus.Not_Available);
        campSiteService.deleteCampSite(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site deleted successfully", null));
    }

    //FILTER BY NAME OR CITY
    @GetMapping("/filterbynameorcity/{filterCondition}")
    public ResponseEntity<BaseResponse> deleteCampType(@PathVariable String filterCondition) {
        BaseResponse response = campSiteService.searchCampSiteByNameOrCity(filterCondition);
        return ResponseEntity.status((int) response.getStatusCode()).body(response);
    }
}



