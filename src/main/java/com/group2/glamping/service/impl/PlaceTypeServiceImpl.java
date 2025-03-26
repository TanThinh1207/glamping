package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.PlaceTypeRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;
import com.group2.glamping.model.entity.PlaceType;
import com.group2.glamping.repository.PlaceTypeRepository;
import com.group2.glamping.service.interfaces.PlaceTypeService;
import com.group2.glamping.service.interfaces.S3Service;
import com.group2.glamping.utils.RedisUtil;
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
public class PlaceTypeServiceImpl implements PlaceTypeService {

    private final PlaceTypeRepository placeTypeRepository;
    private final S3Service s3Service;
    private final RedisUtil redisUtil;

    @Override
    public PlaceTypeResponse createPlaceType(PlaceTypeRequest request) {
        if (request.id() != null) {
            throw new RuntimeException("ID must be null when creating a new place type");
        }
        PlaceType placeType = PlaceType.builder()
                .name(request.name())
                .status(true)
                .build();

//        placeType.setImage(s3Service.uploadFile(image, "PlaceType", "place_type_" + placeType.getId()));
        placeTypeRepository.save(placeType);
        return convertToResponse(placeType);
    }

    @Override
    public PlaceTypeResponse updatePlaceType(PlaceTypeRequest request) {
        if (request.id() == null) {
            throw new RuntimeException("ID is required for update");
        }
        PlaceType placeType = placeTypeRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Place type not found"));
        placeType.setName(request.name());
//        if (image != null && !image.isEmpty()) {
//            String filename = image.getOriginalFilename();
//            placeType.setImage(filename);
//        }
//        placeType.setImage(s3Service.uploadFile(image, "PlaceType", "place_type_" + placeType.getId()));
        redisUtil.deleteCache("filteredCampSites:*");
        redisUtil.deleteCache("campSites:*");
        placeTypeRepository.save(placeType);
        return convertToResponse(placeType);
    }

    @Override
    public PagingResponse<?> getPlaceTypes(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<PlaceType> spec = (root, query, criteriaBuilder) -> {
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
        Page<PlaceType> placeTypePage = placeTypeRepository.findAll(spec, pageable);
        List<PlaceTypeResponse> placeTypeResponses = placeTypePage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PagingResponse<>(
                placeTypeResponses,
                placeTypePage.getTotalElements(),
                placeTypePage.getTotalPages(),
                placeTypePage.getNumber(),
                placeTypePage.getNumberOfElements()
        );
    }

    @Override
    public Object getFilteredPlaceTypes(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> placeTypes = getPlaceTypes(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, placeTypes, "Return using dynamic filter successfully");
    }


    @Override
    public PlaceTypeResponse deletePlaceType(Integer id) {
        PlaceType placeType = placeTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place type not found"));
        placeType.setStatus(false);
        placeTypeRepository.save(placeType);
        redisUtil.deleteCache("filteredCampSites:*");
        redisUtil.deleteCache("campSites:*");
        return convertToResponse(placeType);
    }

    private PlaceTypeResponse convertToResponse(PlaceType placeType) {
        return PlaceTypeResponse.builder()
                .id(placeType.getId())
                .name(placeType.getName())
                .imagePath(placeType.getImage() == null || placeType.getImage().isEmpty() ?
                        "" :
                        s3Service.generatePresignedUrl(placeType.getImage(), 86400))
                .status(placeType.isStatus())
                .build();
    }
}
