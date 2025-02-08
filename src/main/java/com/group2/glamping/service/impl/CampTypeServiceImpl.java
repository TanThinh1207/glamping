package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampTypeRemainingResponse;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.repository.CampTypeRepository;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.service.interfaces.ICampTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CampTypeServiceImpl implements ICampTypeService {

    private final CampTypeRepository campTypeRepository;
    private final CampSiteRepository campSiteRepository;

    @Override
    public Long findAvailableSlots(Integer idCampType, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        Long results = campTypeRepository.getRemainingCampTypes(idCampType, checkInDate, checkOutDate);
//        List<CampTypeRemainingResponse> campTypeRemainingResponses = new ArrayList<>();
        return results;
    }

    //CREATE
    @Override
    public BaseResponse saveCampType(CampTypeCreateRequest request) {
        BaseResponse response = new BaseResponse();

        if (request.getQuantity() <= 0) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Quantity must be greater than 0");
            return response;
        }

        Optional<CampSite> campSiteOpt = campSiteRepository.findById(request.getCampSiteId());
        if (campSiteOpt.isEmpty()) {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("CampSite not found");
            return response;
        }
      
      
        CampType campType = CampType.builder()
                .type(request.getType())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .weekendRate(request.getWeekendRate())
                .holidayRate(request.getHolidayRate())
                .quantity(request.getQuantity())
                .campSite(campSiteOpt.get())
                .status(true)
                .updatedTime(LocalDateTime.now())
                .build();

        campTypeRepository.save(campType);

        CampTypeResponse campTypeResponse = CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .holidayRate(campType.getHolidayRate())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .build();

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("CampType created successfully");
        response.setData(campTypeResponse);

        return response;
    }

    @Override
    public BaseResponse updateCampType(int campTypeId, CampTypeUpdateRequest request) {
        BaseResponse response = new BaseResponse();

        Optional<CampType> campTypeOpt = campTypeRepository.findById(campTypeId);
        if (campTypeOpt.isEmpty()) {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("CampType not found");
            return response;
        }

        CampType campType = campTypeOpt.get();

        if (request.getType() != null) campType.setType(request.getType());
        if (request.getCapacity() > 0) campType.setCapacity(request.getCapacity());
        if (request.getPrice() > 0) campType.setPrice(request.getPrice());
        if (request.getWeekendRate() > 0) campType.setWeekendRate(request.getWeekendRate());
        if (request.getHolidayRate() > 0) campType.setHolidayRate(request.getHolidayRate());
        if (request.getQuantity() >= 0) campType.setQuantity(request.getQuantity());
        campType.setStatus(request.isStatus());

        campType.setUpdatedTime(LocalDateTime.now());
        campTypeRepository.save(campType);

        CampTypeResponse campTypeResponse = CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .holidayRate(campType.getHolidayRate())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .build();

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("CampType updated successfully");
        response.setData(campTypeResponse);

        return response;
    }

    @Override
    public BaseResponse findByCampSiteId(int campSiteId) {
        BaseResponse response = new BaseResponse();

        List<CampType> campTypeList = campTypeRepository.findByCampSiteId(campSiteId);

        if (campTypeList.isEmpty()) {
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("No CampTypes found for this CampSite");
            response.setData(Collections.emptyList());
            return response;
        }

        List<CampTypeResponse> campTypeResponses = campTypeList.stream().map(campType ->
                CampTypeResponse.builder()
                        .id(campType.getId())
                        .type(campType.getType())
                        .capacity(campType.getCapacity())
                        .price(campType.getPrice())
                        .weekendRate(campType.getWeekendRate())
                        .holidayRate(campType.getHolidayRate())
                        .quantity(campType.getQuantity())
                        .status(campType.isStatus())
                        .build()
        ).toList();

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Retrieved CampTypes successfully");
        response.setData(campTypeResponses);
        return response;
    }



    @Override
    public BaseResponse softDeleteCampType(int campTypeId) {
        Optional<CampType> campTypeOpt = campTypeRepository.findById(campTypeId);
        BaseResponse response = new BaseResponse();

        if (campTypeOpt.isPresent()) {
            CampType campType = campTypeOpt.get();
            campType.setStatus(false);
            campTypeRepository.save(campType);

            CampTypeResponse campTypeResponse = new CampTypeResponse();
            campTypeResponse.setId(campType.getId());
            campTypeResponse.setType(campType.getType());
            campTypeResponse.setCapacity(campType.getCapacity());
            campTypeResponse.setPrice(campType.getPrice());
            campTypeResponse.setWeekendRate(campType.getWeekendRate());
            campTypeResponse.setHolidayRate(campType.getHolidayRate());
            campTypeResponse.setQuantity(campType.getQuantity());
            campTypeResponse.setStatus(campType.isStatus());

            response.setStatusCode(200);
            response.setMessage("Camp Type status updated to NOT_AVAILABLE");
            response.setData(campTypeResponse);
        } else {
            response.setStatusCode(404);
            response.setMessage("Camp Type not found");
            response.setData(null);
        }
        return response;
    }
}
