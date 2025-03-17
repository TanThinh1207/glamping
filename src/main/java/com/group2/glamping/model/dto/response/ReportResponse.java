package com.group2.glamping.model.dto.response;



import com.group2.glamping.model.entity.Report;
import com.group2.glamping.model.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private int id;
    private int campSiteId;
    private int userId;
    private ReportStatus status;
    private LocalDateTime createdTime;
    private String message;
    private String reportType;

    public static ReportResponse fromEntity(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .campSiteId(report.getCampSite().getId())
                .userId(report.getUser().getId())
                .status(report.getStatus())
                .createdTime(report.getCreatedTime())
                .message(report.getMessage())
                .reportType(report.getReportType())
                .build();
    }
}
