package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.CampTypeCreateRequest;
import com.group2.glamping.model.dto.requests.CampTypeUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.CampTypeResponse;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.CampType;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.CampTypeRepository;
import com.group2.glamping.service.interfaces.CampTypeService;
import com.group2.glamping.service.interfaces.S3Service;
import com.group2.glamping.utils.ResponseFilterUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
    private final S3Service s3Service;

    @Override
    public Long findAvailableSlots(Integer idCampType, LocalDateTime checkInDate, LocalDateTime checkOutDate) {
        return campTypeRepository.getRemainingCampTypes(idCampType, checkInDate, checkOutDate);
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
        if (request.capacity() != null) campType.setCapacity(request.capacity());
        if (request.price() != null) campType.setPrice(request.price());
        if (request.weekendRate() != null) campType.setWeekendRate(request.weekendRate());
        if (request.quantity() != null) campType.setQuantity(request.quantity());
        if (request.status() != null) campType.setStatus(request.status());

        campType.setUpdatedTime(LocalDateTime.now());
        campTypeRepository.save(campType);

        CampTypeResponse campTypeResponse = CampTypeResponse.builder()
                .id(campType.getId())
                .type(campType.getType())
                .capacity(campType.getCapacity())
                .price(campType.getPrice())
                .updatedAt(campType.getUpdatedTime())
                .weekendRate(campType.getWeekendRate())
                .quantity(campType.getQuantity())
                .image(s3Service.getFileUrl(campType.getImage()))
                .status(campType.isStatus())
                .facilities(FacilityResponse.fromEntity(campType.getFacilities(), s3Service))
                .build();

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("CampType updated successfully");
        response.setData(campTypeResponse);

        return response;
    }

    @Override
    public PagingResponse<?> getCampTypes(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<CampType> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            params.forEach((key, value) -> {
                switch (key) {
                    case "id" -> predicates.add(criteriaBuilder.equal(root.get("id"), value));
                    case "type" -> predicates.add(criteriaBuilder.equal(root.get("type"), value));
                    case "capacity" -> predicates.add(criteriaBuilder.equal(root.get("capacity"), value));
                    case "price" -> predicates.add(criteriaBuilder.equal(root.get("price"), value));
                    case "updatedAt" ->
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), LocalDateTime.parse(value)));
                    case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                    case "status" ->
                            predicates.add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(value)));
                    case "campSiteId" -> {
                        Join<CampType, CampSite> campSiteJoin = root.join("campSite", JoinType.INNER);
                        predicates.add(criteriaBuilder.equal(campSiteJoin.get("id"), value));
                    }
                }


            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CampType> campTypePage = campTypeRepository.findAll(spec, pageable);
        List<CampTypeResponse> campTypeResponses = campTypePage.getContent().stream()
                .map(campType -> CampTypeResponse.fromEntity(campType, s3Service))
                .toList();
        if (params.containsKey("checkIn") && params.containsKey("checkOut")) {
            for (CampTypeResponse campTypeResponse : campTypeResponses) {
                Long availableSlots = findAvailableSlots(
                        campTypeResponse.getId(),
                        LocalDateTime.parse(params.get("checkIn")),
                        LocalDateTime.parse(params.get("checkOut")));
                campTypeResponse.setAvailableSlot(availableSlots == null ? campTypeResponse.getQuantity() : availableSlots);
            }
        }
        return new PagingResponse<>(
                campTypeResponses,
                campTypePage.getTotalElements(),
                campTypePage.getTotalPages(),
                campTypePage.getNumber(),
                campTypePage.getNumberOfElements()
        );
    }

    @Override
    public Object getFilteredCampTypes(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> campTypes = getCampTypes(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, campTypes, "Return using dynamic filter successfully");

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
                .image(campType.getImage() == null || campType.getImage().isEmpty() ?
                        "No image" :
                        s3Service.generatePresignedUrl(campType.getImage(), 86400))
                .build();

    }
}
