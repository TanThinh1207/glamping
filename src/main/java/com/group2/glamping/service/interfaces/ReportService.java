package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.ReportRequest;
import com.group2.glamping.model.dto.response.ReportResponse;

import java.util.List;

public interface ReportService {
    ReportResponse createReport(ReportRequest request);
    ReportResponse getReportById(int id);
    List<ReportResponse> getAllReports();
    ReportResponse updateReport(int id, ReportRequest request);
    void deleteReport(int id);
}
