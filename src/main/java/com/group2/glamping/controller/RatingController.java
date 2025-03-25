package com.group2.glamping.controller;

import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.FullRatingResponse;
import com.group2.glamping.model.dto.response.PagingResponseSingle;
import com.group2.glamping.service.interfaces.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Rating API", description = "API for retrieving campsite ratings and reviews")
public class RatingController {

    private final RatingService ratingService;

    @Operation(
            summary = "Retrieve campsite ratings",
            description = """
                         Returns a paginated list of ratings for a specific campsite by its ID.\s
                         **Valid sortBy fields:** \s
                         - `userId` → Sort by user ID \s
                         - `userName` → Sort by user name \s
                         - `uploadTime` → Sort by upload time \s
                         - `checkOutTime` → Sort by checkout time \s
                         - `rating` → Sort by rating score \s
                         Default sorting: `checkOutTime` (descending)
                    \s"""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the ratings"),
            @ApiResponse(responseCode = "404", description = "Campsite not found")
    })
    @GetMapping("/{campSiteId}")
    public ResponseEntity<BaseResponse> getRatings(
            @PathVariable
            @Parameter(description = "The ID of the campsite to retrieve ratings for", example = "1")
            Integer campSiteId,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Current page number (starting from 0)", example = "0")
            int page,

            @RequestParam(defaultValue = "10")
            @Parameter(description = "Number of ratings per page", example = "10")
            int size,

            @RequestParam(defaultValue = "checkOutTime")
            @Parameter(
                    description = "Field to sort by. See valid options in the description.",
                    example = "rating"
            )
            String sortBy,

            @RequestParam(defaultValue = "desc")
            @Parameter(description = "Sorting direction (`asc` for ascending, `desc` for descending)", example = "desc")
            String direction) {

        PagingResponseSingle<FullRatingResponse> response = ratingService.getFullRating(campSiteId, page, size, sortBy, direction);

        return ResponseEntity.ok(
                BaseResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .data(response)
                        .message("Retrieve rating successfully")
                        .build()
        );
    }
}
