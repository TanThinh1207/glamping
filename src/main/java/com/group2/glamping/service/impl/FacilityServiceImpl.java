package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.entity.Facility;
import com.group2.glamping.repository.FacilityRepository;
import com.group2.glamping.service.interfaces.FacilityService;
import com.group2.glamping.service.interfaces.S3Service;
import com.group2.glamping.utils.ResponseFilterUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;
    private final S3Service s3Service;

    @Override
    public FacilityResponse createFacility(FacilityRequest request) {
        if (request.id() != null) {
            throw new RuntimeException("ID must be null when creating a new facility");
        }
        Facility facility = new Facility();
        facility.setName(request.name());
        facility.setDescription(request.description());
        facility.setStatus(true);
//        facility.setImageUrl(s3Service.uploadFile(file, "Facility", "facility" + facility.getId()));
        facilityRepository.save(facility);
        return convertToResponse(facility);
    }

    @Override
    public FacilityResponse updateFacility(FacilityRequest request) {
        if (request.id() == null) {
            throw new RuntimeException("ID is required for update");
        }
        Facility facility = facilityRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        facility.setName(request.name());
        facility.setDescription(request.description());
//        facility.setImageUrl(s3Service.uploadFile(file, "Facility", "facility" + facility.getId()));

        facilityRepository.save(facility);
        return convertToResponse(facility);
    }

    @Override
    public PagingResponse<?> getFacilities(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<Facility> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "id" -> predicates.add(criteriaBuilder.equal(root.get("id"), value));
                    case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                    case "status" ->
                            predicates.add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(value)));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Facility> facilityPage = facilityRepository.findAll(spec, pageable);
        List<FacilityResponse> facilityResponses = facilityPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PagingResponse<>(
                facilityResponses,
                facilityPage.getTotalElements(),
                facilityPage.getTotalPages(),
                facilityPage.getNumber(),
                facilityPage.getNumberOfElements()
        );
    }

    @Override
    public Object getFilteredFacilities(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> facilities = getFacilities(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, facilities, "Return using dynamic filter successfully");
    }


    @Override
    public FacilityResponse deleteFacility(Integer id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        facility.setStatus(false);
        facilityRepository.save(facility);
        return convertToResponse(facility);
    }

    private FacilityResponse convertToResponse(Facility facility) {
        return FacilityResponse.builder()
                .id(facility.getId())
                .name(facility.getName())
                .description(facility.getDescription())
                .status(facility.isStatus())
                .image(
                        facility.getImageUrl() == null || facility.getImageUrl().isEmpty() ?
                                s3Service.generatePresignedUrl("Facility/FACILITY__1741050569490_Empty.png", 86400) :
                                s3Service.generatePresignedUrl(facility.getImageUrl(), 86400))
                .build();
    }
}
