package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ICampTypeService {
    Long findAvailableSlots(Integer campTypeId, LocalDateTime checkInDate, LocalDateTime checkOutDate);

    BaseResponse saveCampType(CampTypeCreateRequest request);

    BaseResponse findByCampSiteId(int campSiteId);

    BaseResponse updateCampType(int campTypeId, CampTypeUpdateRequest request);

    BaseResponse softDeleteCampType(int campTypeId);

}
