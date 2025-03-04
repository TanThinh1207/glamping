package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.model.entity.Utility;
import com.group2.glamping.repository.UtilityRepository;
import com.group2.glamping.service.interfaces.S3Service;
import com.group2.glamping.service.interfaces.UtilityService;
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
public class UtilityServiceImpl implements UtilityService {

    private final UtilityRepository utilityRepository;
    private final S3Service s3Service;

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
    public PagingResponse<?> getUtilities(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<Utility> spec = (root, query, criteriaBuilder) -> {
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
    public Object getFilteredUtilities(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> utilities = getUtilities(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, utilities, "Retrieve filtered list successfully");
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
                .imagePath(utility.getImageUrl() == null || utility.getImageUrl().isEmpty() ?
                        "" :
                        s3Service.generatePresignedUrl(utility.getImageUrl()))
                .status(utility.isStatus())
                .build();
    }
}
