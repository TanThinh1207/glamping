package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.entity.CampType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICampTypeService {
    Long findAvailableSlots(Integer campTypeId, LocalDateTime checkInDate, LocalDateTime checkOutDate);
    Optional<CampTypeResponse> saveCampType(CampTypeCreateRequest request);
    List<CampType> findByCampSiteId(int campSiteId);
    Map<String, Object> softDeleteCampType(int campTypeId);
}