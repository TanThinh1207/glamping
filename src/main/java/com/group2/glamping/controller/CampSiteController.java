package com.group2.glamping.controller;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.model.dto.requests.CampSiteRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.service.interfaces.CampSiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campsites")
@Tag(name = "CampSite API", description = "API for managing Campsites")
@RequiredArgsConstructor
public class CampSiteController {

    private final CampSiteService campSiteService;

    @Operation(
            summary = "Get all available campsites",
            description = "Retrieve a list of available campsites",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp sites retrieved successfully")
            }
    )
    @GetMapping
    public ResponseEntity<BaseResponse> getAllAvailableCampSites() {
        List<CampSiteResponse> campsites = campSiteService.getAvailableCampSites();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp sites retrieved successfully", campsites));
    }

    @Operation(
            summary = "Get all pending campsites",
            description = "Retrieve a list of campsites that are pending approval",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pending camp sites retrieved successfully")
            }
    )
    @GetMapping("/pending")
    public ResponseEntity<BaseResponse> getAllPendingCampSites() {
        List<CampSiteResponse> campsites = campSiteService.getPendingCampSites();
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp sites retrieved successfully", campsites));
    }

    @Operation(
            summary = "Get campsite by ID",
            description = "Retrieve details of a specific campsite by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site found"),
                    @ApiResponse(responseCode = "404", description = "Camp site not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getCampSiteById(
            @Parameter(description = "ID of the campsite", example = "1") @PathVariable int id) {
        Optional<CampSiteResponse> campsite = campSiteService.getCampSiteBasicDetail(id);
        return campsite.map(site -> ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site found", site)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(), "Camp site not found", null)));
    }

    @Operation(
            summary = "Create a new campsite",
            description = "Add a new campsite to the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or bad request")
            }
    )
    @PostMapping
    public ResponseEntity<BaseResponse> createCampSite(@Valid @RequestBody CampSiteRequest request) {
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

    @Operation(
            summary = "Update a campsite",
            description = "Modify details of an existing campsite by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Camp site not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateCampSite(
            @Parameter(description = "ID of the campsite", example = "1") @PathVariable int id,
            @Valid @RequestBody CampSiteRequest updatedCampSite) {
        try {
            return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(),
                    "Camp site updated successfully",
                    campSiteService.updateCampSite(id, updatedCampSite)));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponse(HttpStatus.NOT_FOUND.value(),
                    "Camp site not found",
                    null));
        }
    }

    @Operation(
            summary = "Delete a campsite",
            description = "Remove a campsite by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Camp site deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Camp site not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteCampSite(
            @Parameter(description = "ID of the campsite", example = "1") @PathVariable int id) {
        campSiteService.deleteCampSite(id);
        return ResponseEntity.ok(new BaseResponse(HttpStatus.OK.value(), "Camp site deleted successfully", null));
    }

    @Operation(
            summary = "Search campsites by name or city",
            description = "Filter campsites based on name or city",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results returned successfully")
            }
    )
    @GetMapping("/filterbynameorcity/{filterCondition}")
    public ResponseEntity<BaseResponse> searchCampSiteByNameOrCity(
            @Parameter(description = "Search keyword (camp name or city)", example = "Hanoi") @PathVariable String filterCondition) {
        BaseResponse response = campSiteService.searchCampSiteByNameOrCity(filterCondition);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
