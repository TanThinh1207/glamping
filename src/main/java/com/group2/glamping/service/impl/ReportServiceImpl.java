package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.ReportRequest;
import com.group2.glamping.model.dto.response.ReportResponse;
import com.group2.glamping.model.entity.Report;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.ReportStatus;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.ReportRepository;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final CampSiteRepository campSiteRepository;
    private final UserRepository userRepository;

    @Override
    public ReportResponse createReport(ReportRequest request) {
        Report report = mapRequestToEntity(request);
        report.setStatus(ReportStatus.Pending);
        report.setCreatedTime(LocalDateTime.now());
        reportRepository.save(report);
        return ReportResponse.fromEntity(report);
    }

    @Override
    public ReportResponse getReportById(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        return ReportResponse.fromEntity(report);
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(ReportResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReportResponse updateReport(int id, ReportRequest request) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));

        if (request.status() != null) {
            report.setStatus(request.status());
        }
        if (request.message() != null) {
            report.setMessage(request.message());
        }
        if (request.reportType() != null) {
            report.setReportType(request.reportType());
        }

        reportRepository.save(report);
        return ReportResponse.fromEntity(report);
    }

    @Override
    @Transactional
    public void deleteReport(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        report.setStatus(ReportStatus.Denied);
        reportRepository.save(report);
    }

    //<editor-fold defaultstate="collapsed" desc="Mapping request to entity">
    private Report mapRequestToEntity(ReportRequest request) {
        CampSite campSite = campSiteRepository.findById(request.campSiteId())
                .orElseThrow(() -> new RuntimeException("Campsite not found with ID: " + request.campSiteId()));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.userId()));

        return Report.builder()
                .campSite(campSite)
                .user(user)
                .status(ReportStatus.Pending)
                .message(request.message())
                .reportType(request.reportType())
                .createdTime(LocalDateTime.now())
                .build();
    }
    //</editor-fold>
}
