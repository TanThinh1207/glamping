package com.group2.glamping.service.impl;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampTypeRemainingResponse;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.CampTypeRepository;
import com.group2.glamping.service.interfaces.CampTypeService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CampTypeServiceImpl implements CampTypeService {

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

        if (request.type() != null) campType.setType(request.type());
        campType.setCapacity(request.capacity());
        if (request.price() > 0) campType.setPrice(request.price());
        if (request.weekendRate() > 0) campType.setWeekendRate(request.weekendRate());
        campType.setQuantity(request.quantity());
        campType.setStatus(request.status());

        campType.setUpdatedTime(LocalDateTime.now());
        campTypeRepository.save(campType);

        CampTypeResponse campTypeResponse = CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .build();

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("CampType updated successfully");
        response.setData(campTypeResponse);

        return response;
    }

    @Override
    public PagingResponse<?> getCampTypes(Map<String, String> params, int page, int size) {
        Specification<CampType> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                //                    case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                //                    case "status" ->
                //                            predicates.add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(value)));
                if (key.equals("campSiteId")) {
                    predicates.add(criteriaBuilder.equal(root.get("campSite").get("id"), Integer.parseInt(value)));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size);
        Page<CampType> utilityPage = campTypeRepository.findAll(spec, pageable);
        List<CampTypeResponse> utilityResponses = utilityPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PagingResponse<>(
                utilityResponses,
                utilityPage.getTotalElements(),
                utilityPage.getTotalPages(),
                utilityPage.getNumber(),
                utilityPage.getNumberOfElements()
        );
    }

    @Override
    public MappingJacksonValue getFilteredCampTypes(Map<String, String> params, int page, int size, String fields) {
        // Get data from repository or other service
        PagingResponse<?> campSites = getCampTypes(params, page, size);

        // Apply dynamic filtering
        SimpleFilterProvider filters;
        if (fields != null && !fields.isEmpty()) {
            filters = new SimpleFilterProvider()
                    .addFilter("dynamicFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",")));
        } else {
            filters = new SimpleFilterProvider()
                    .addFilter("dynamicFilter", SimpleBeanPropertyFilter.serializeAll());
        }

        // Wrap response with MappingJacksonValue
        MappingJacksonValue mapping = new MappingJacksonValue(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .data(campSites)
                .message("Retrieve all campsites successfully")
                .build());

        mapping.setFilters(filters);

        return mapping;
    }

    @Override
    public BaseResponse softDeleteCampType(int campTypeId) {
        Optional<CampType> campTypeOpt = campTypeRepository.findById(campTypeId);
        BaseResponse response = new BaseResponse();

        if (campTypeOpt.isPresent()) {
            CampType campType = campTypeOpt.get();
            campType.setStatus(false);
            campTypeRepository.save(campType);

            response.setStatusCode(200);
            response.setMessage("Camp Type status updated to NOT_AVAILABLE");
            response.setData(convertToResponse(campType));
        } else {
            response.setStatusCode(404);
            response.setMessage("Camp Type not found");
            response.setData(null);
        }
        return response;
    }

    // Mapping to entity to entityResponse
    private CampTypeResponse convertToResponse(CampType campType) {
        return CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .weekendRate(campType.getWeekendRate())
                .quantity(campType.getQuantity())
                .status(campType.isStatus())
                .campSiteId(campType.getCampSite().getId())
                .build();

    }
}
