package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.ReportRequest;
import com.group2.glamping.model.dto.requests.ReportUpdateRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;
import com.group2.glamping.model.dto.response.ReportResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.PlaceType;
import com.group2.glamping.model.entity.Report;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.model.enums.ReportStatus;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.ReportRepository;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.ReportService;
import com.group2.glamping.utils.ResponseFilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.criteria.Predicate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j // Automatically initializes a Logger instance
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final CampSiteRepository campSiteRepository;
    private final UserRepository userRepository;
    private final CampSiteMapper campSiteMapper;

    @Override
    public ReportResponse createReport(ReportRequest request) {
        try {
            log.info("Starting report creation for campSiteId: {}, userId: {}", request.campSiteId(), request.userId());

            CampSite campSite = campSiteRepository.findById(request.campSiteId())
                    .orElseThrow(() -> new AppException(ErrorCode.CAMP_SITE_NOT_FOUND));

            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            Report report = Report.builder()
                    .campSite(campSite)
                    .user(user)
                    .status(ReportStatus.Pending)
                    .createdTime(LocalDateTime.now())
                    .message(request.message())
                    .reportType(request.reportType())
                    .build();

            report = reportRepository.save(report);
            log.info("Report successfully created with id: {}", report.getId());

            return ReportResponse.fromEntity(report);
        } catch (Exception e) {
            log.error("Failed to create report", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", e);
        }
    }

    @Override
    public ReportResponse updateReport(ReportUpdateRequest request) {
        if (request.id() == null) {
            throw new AppException(ErrorCode.ID_REQUIRED_FOR_UPDATE);
        }
        Report report = reportRepository.findById(request.id())
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        if (request.message() != null) {
            report.setMessage(request.message());
        }
        if (request.reportType() != null) {
            report.setReportType(request.reportType());
        }
        report = reportRepository.save(report);
        return ReportResponse.fromEntity(report);
    }


    @Override
    public PagingResponse<?> getReports(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<Report> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "id" -> predicates.add(criteriaBuilder.equal(root.get("id"), Integer.parseInt(value)));
                    case "message" -> predicates.add(criteriaBuilder.like(root.get("message"), "%" + value + "%"));
                    case "reportType" -> predicates.add(criteriaBuilder.like(root.get("reportType"), "%" + value + "%"));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Report> reportPage = reportRepository.findAll(spec, pageable);

        List<ReportResponse> reportResponses = reportPage.getContent().stream()
                .map(ReportResponse::fromEntity)
                .toList();

        return new PagingResponse<>(
                reportResponses,
                reportPage.getTotalElements(),
                reportPage.getTotalPages(),
                reportPage.getNumber(),
                reportPage.getNumberOfElements()
        );
    }


    @Override
    public Object getFilteredReports(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> reports = getReports(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, reports, "Return using dynamic filter successfully");
    }

    @Override
    public ReportResponse acceptReport(int reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        report.setStatus(ReportStatus.Resolved);
        reportRepository.save(report);

        CampSite campSite = report.getCampSite();

        long acceptedReportsCount = reportRepository.countByCampSiteAndStatus(campSite, ReportStatus.Resolved);

        if (acceptedReportsCount >= 3) {
            campSite.setStatus(CampSiteStatus.Not_Available);
            campSiteRepository.save(campSite);
            log.info("CampSite {} has been set to Not Available due to multiple accepted reports", campSite.getId());
        }

        return ReportResponse.fromEntity(report);
    }


    @Override
    public ReportResponse softDeleteReport(int id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));
        report.setStatus(ReportStatus.Denied);
        reportRepository.save(report);
        return ReportResponse.fromEntity(report);
    }
}
