package com.group2.glamping.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.group2.glamping.model.entity.*;
import com.group2.glamping.model.enums.ReportStatus;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.service.interfaces.S3Service;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@JsonFilter("dynamicFilter")
public class ReportResponse {
    private int id;
    private CampSiteBasicResponse campSite;
    private UserBasicResponse user;
    private ReportStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String message;
    private String reportType;

    public static ReportResponse fromEntity(Report report) {
        if (report == null) {
            return null;
        }

        CampSite campSite = report.getCampSite();
        User user = report.getUser();

        return ReportResponse.builder()
                .id(report.getId())
                .campSite(campSite != null ? new CampSiteBasicResponse(campSite.getId(), campSite.getName(), campSite.getAddress()) : null)
                .user(user != null ? new UserBasicResponse(user.getId(), user.getEmail(), user.getFirstname(), user.getLastname()) : null)
                .status(report.getStatus())
                .createdTime(report.getCreatedTime())
                .message(report.getMessage())
                .reportType(report.getReportType())
                .build();
    }
}
