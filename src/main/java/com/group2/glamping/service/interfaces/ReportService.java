package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.ReportRequest;
import com.group2.glamping.model.dto.requests.ReportUpdateRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.ReportResponse;

import java.util.List;
import java.util.Map;

public interface ReportService {

    ReportResponse createReport(ReportRequest request);
    ReportResponse updateReport(ReportUpdateRequest request);
    PagingResponse<?> getReports(Map<String, String> params, int page, int size, String sortBy, String direction);
    Object getFilteredReports(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);
    ReportResponse softDeleteReport(int id);
    ReportResponse acceptReport(int reportId);

}
