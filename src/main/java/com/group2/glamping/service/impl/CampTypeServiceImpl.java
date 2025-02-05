package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampTypeRemainingResponse;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.repository.BookingRepository;
import com.group2.glamping.repository.CampTypeRepository;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.ICampTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<Object[]> results = campTypeRepository.getRemainingCampTypes(idCampType, checkInDate, checkOutDate);
        List<CampTypeRemainingResponse> campTypeRemainingResponses = new ArrayList<>();

        for (Object[] result : results) {
            Integer campTypeId = (Integer) result[0];
            String type = (String) result[1];
            Integer capacity = (Integer) result[2];
            Long remainingQuantity = (Long) result[3];

            CampTypeRemainingResponse response = new CampTypeRemainingResponse();
            response.setCampTypeId(campTypeId);
            response.setType(type);
            response.setCapacity(capacity);
            response.setRemainingQuantity(remainingQuantity);
            campTypeRemainingResponses.add(response);
        }

        return campTypeRemainingResponses.stream()
                .mapToLong(CampTypeRemainingResponse::getRemainingQuantity)
                .sum();
    }

    @Override
    public Optional<CampTypeResponse> saveCampType(CampTypeCreateRequest request) {
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CampType campType = CampType.builder()
                .type(request.getType())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .weekendRate(request.getWeekendRate())
                .holidayRate(request.getHolidayRate())
                .quantity(request.getQuantity())
                .campSite(campSiteRepository.findById(request.getCampSiteId()).orElse(null))
                .status(true)
                .build();

        campType.setUpdatedTime(LocalDateTime.now());
        campTypeRepository.save(campType);

        CampTypeResponse response = new CampTypeResponse();
        response.setId(campType.getId());
        response.setType(campType.getType());
        response.setCapacity(campType.getCapacity());
        response.setPrice(campType.getPrice());
        response.setWeekendRate(campType.getWeekendRate());
        response.setHolidayRate(campType.getHolidayRate());
        response.setQuantity(campType.getQuantity());
        response.setStatus(campType.isStatus());

        return Optional.of(response);
    }

    @Override
    public Optional<CampTypeResponse> updateCampType(int campTypeId, CampTypeUpdateRequest request) {
        Optional<CampType> campTypeOpt = campTypeRepository.findById(campTypeId);

        if (campTypeOpt.isEmpty()) {
            return Optional.empty();
        }

        CampType campType = campTypeOpt.get();

        //Gán thông tin
        campType.setType(request.getType());
        campType.setCapacity(request.getCapacity());
        campType.setPrice(request.getPrice());
        campType.setWeekendRate(request.getWeekendRate());
        campType.setHolidayRate(request.getHolidayRate());
        campType.setQuantity(request.getQuantity());
        campType.setStatus(request.isStatus());
        campType.setUpdatedTime(LocalDateTime.now());

        campTypeRepository.save(campType);

        // Trả về DTO response
        CampTypeResponse response = CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .holidayRate(campType.getHolidayRate())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .build();

        return Optional.of(response);
    }

    @Override
    public List<CampTypeResponse> findByCampSiteId(int campSiteId) {
        List<CampType> campTypeList = campTypeRepository.findByCampSiteId(campSiteId);

        if (campTypeList.isEmpty()) {
            return Collections.emptyList();
        }

        return campTypeList.stream().map(campType -> CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .holidayRate(campType.getHolidayRate())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .build()).toList();
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
