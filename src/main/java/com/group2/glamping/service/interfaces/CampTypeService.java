package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.dto.response.PagingResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CampTypeService {
    Long findAvailableSlots(Integer campTypeId, LocalDateTime checkInDate, LocalDateTime checkOutDate);

    BaseResponse saveCampType(CampTypeCreateRequest request);

    BaseResponse updateCampType(int campTypeId, CampTypeUpdateRequest request);

    PagingResponse<?> getCampTypes(Map<String, String> params, int page, int size, String sortBy, String direction);

    Object getFilteredCampTypes(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    BaseResponse softDeleteCampType(int campTypeId);

    CampTypeResponse updateFacility(int campTypeId, List<Integer> facilityIds);
}
