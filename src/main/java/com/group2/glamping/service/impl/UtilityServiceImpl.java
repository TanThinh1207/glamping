package com.group2.glamping.service.impl;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.model.entity.Facility;
import com.group2.glamping.model.entity.Utility;
import com.group2.glamping.repository.UtilityRepository;
import com.group2.glamping.service.interfaces.UtilityService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilityServiceImpl implements UtilityService {

    private final UtilityRepository utilityRepository;

    @Override
    public UtilityResponse createUtility(UtilityRequest request) {
        UtilityResponse.builder()
                .name(request.name())
//                .imagePath(request.imagePath() != null ? request.imagePath().getOriginalFilename() : null)
                .status(true)
                .build();
        Utility utility = mapRequestToEntity(request);
        utility.setStatus(true);
        utilityRepository.save(utility);
        return convertToResponse(utility);
    }

    @Override
    public UtilityResponse updateUtility(UtilityRequest request) {
        Utility existingUtility = getUtilityById(request.id());

        if (request.name() != null) {
            existingUtility.setName(request.name());
        }

//        if (request.imagePath() != null && !request.imagePath().isEmpty()) {
//            existingUtility.setImageUrl(request.imagePath().getOriginalFilename());
//        }

        utilityRepository.save(existingUtility);

        return convertToResponse(existingUtility);
    }


    @Override
    public Utility getUtilityById(int id) {
        return utilityRepository.findById(id).orElseThrow(() -> new RuntimeException("Utility not found"));
    }

    @Override
    public PagingResponse<?> getUtilities(Map<String, String> params, int page, int size) {
        Specification<Utility> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                    case "status" ->
                            predicates.add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(value)));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size);
        Page<Utility> utilityPage = utilityRepository.findAll(spec, pageable);
        List<UtilityResponse> utilityResponses = utilityPage.getContent().stream()
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
    public MappingJacksonValue getFilteredUtilities(Map<String, String> params, int page, int size, String fields) {
        PagingResponse<?> utilities = getUtilities(params, page, size);

        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter("dynamicFilter", fields != null && !fields.isEmpty() ?
                        SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",")) :
                        SimpleBeanPropertyFilter.serializeAll());

        MappingJacksonValue mapping = new MappingJacksonValue(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .data(utilities)
                .message("Retrieve all utilities successfully")
                .build());
        mapping.setFilters(filters);

        return mapping;
    }

    @Override
    public UtilityResponse softDeleteUtility(int id) {
        Utility utility = getUtilityById(id);
        utility.setStatus(false);
        utilityRepository.save(utility);
        return convertToResponse(utility);
    }


    // Mapping request to entity
    private Utility mapRequestToEntity(UtilityRequest request) {
        Utility.UtilityBuilder utilityBuilder = Utility.builder();

        if (request.id() != null) {
            Utility existingUtility = utilityRepository.findById(request.id()).orElse(new Utility());
            utilityBuilder.id(existingUtility.getId())
                    .imageUrl(existingUtility.getImageUrl());
        }

        utilityBuilder.name(request.name());

//        if (request.imagePath() != null && !request.imagePath().isEmpty()) {
//            String filename = request.imagePath().getOriginalFilename();
//            utilityBuilder.imageUrl(filename);
//        }

        return utilityBuilder.build();
    }


    // Mapping to entity to entityResponse
    private UtilityResponse convertToResponse(Utility utility) {
        return UtilityResponse.builder()
                .id(utility.getId())
                .name(utility.getName())
                .imagePath(utility.getImageUrl())
                .status(utility.isStatus())
                .build();
    }
}
