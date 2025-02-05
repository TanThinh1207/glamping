package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.response.CampTypeRemainingRespone;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.model.enums.CampSiteStatus;
import com.group2.glamping.repository.BookingRepository;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.CampTypeRepository;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.ICampTypeService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Transactional
@Data
@Service
public class CampTypeServiceImpl implements ICampTypeService {
    @Autowired
    private final CampTypeRepository campTypeRepository;

    @Autowired
    private final BookingRepository bookingRepository;

    @Autowired
    private final UserRepository userService;

    @Autowired
    private final CampSiteRepository campSiteRepository;

    @Override
    public Long findAvailableSlots(Integer idCampType, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        List<Object[]> results = campTypeRepository.getRemainingCampTypes(idCampType, checkInDate, checkOutDate);
        List<CampTypeRemainingRespone> campTypeRemainingRespones = new ArrayList<>();

        for (Object[] result : results) {
            Integer campTypeId = (Integer) result[0];
            String type = (String) result[1];
            Integer capacity = (Integer) result[2];
            Long remainingQuantity = (Long) result[3];

            CampTypeRemainingRespone response = new CampTypeRemainingRespone();
            response.setCampTypeId(campTypeId);
            response.setType(type);
            response.setCapacity(capacity);
            response.setRemainingQuantity(remainingQuantity);
            campTypeRemainingRespones.add(response);
        }
        return campTypeRemainingRespones.stream()
                .mapToLong(CampTypeRemainingRespone::getRemainingQuantity)
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
    public List<CampType> findByCampSiteId(int campSiteId) {
        return campTypeRepository.findByCampSiteId(campSiteId);
    }


    //DELETE
    @Override
    public Map<String, Object> softDeleteCampType(int campTypeId) {
        Optional<CampType> campTypeOpt = campTypeRepository.findById(campTypeId);
        Map<String, Object> response = new HashMap<>();

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

            response.put("statusCode", 200);
            response.put("mess", "Camp Type status updated to NOT_AVAILABLE");
            response.put("data", campTypeResponse);
        } else {
            response.put("statusCode", 404);
            response.put("mess", "Camp Type not found");
            response.put("data", null);
        }
        return response;
    }
}

