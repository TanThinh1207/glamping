package com.group2.glamping.controller;

import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.RevenueGraphDto;
import com.group2.glamping.service.impl.RevenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/revenue")
@RequiredArgsConstructor
@Tag(name = "Revenue", description = "Revenue API for hosts")
public class RevenueController {

    private final RevenueService revenueService;

    @Operation(summary = "Get revenue graph", description = "Retrieve revenue data for a host within a specified time range.")
    @GetMapping("/{hostId}")
    public ResponseEntity<?> getRevenueGraph(
            @PathVariable @Parameter(description = "Host ID") Long hostId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Start date in ISO format (yyyy-MM-dd)") LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "End date in ISO format (yyyy-MM-dd)") LocalDate endDate,
            @RequestParam(required = false) @Parameter(description = "CampSite ID (optional)") Long campSiteId,
            @RequestParam(defaultValue = "daily") @Parameter(description = "Interval (daily/monthly)") String interval) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<RevenueGraphDto> data = revenueService.getRevenueGraph(hostId, startDateTime, endDateTime, campSiteId, interval);

        return ResponseEntity.ok(BaseResponse.builder()
                .data(data)
                .message("Get successfully")
                .statusCode(HttpStatus.OK.value())
                .build());
    }

}
