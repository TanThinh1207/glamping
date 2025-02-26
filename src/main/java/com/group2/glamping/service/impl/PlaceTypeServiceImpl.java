package com.group2.glamping.service.impl;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.model.dto.requests.PlaceTypeRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.model.entity.PlaceType;
import com.group2.glamping.repository.PlaceTypeRepository;
import com.group2.glamping.service.interfaces.PlaceTypeService;
import com.group2.glamping.service.interfaces.S3Service;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlaceTypeServiceImpl implements PlaceTypeService {

    private final PlaceTypeRepository placeTypeRepository;
    private final S3Service s3Service;

    @Override
    public PlaceTypeResponse createPlaceType(PlaceTypeRequest request, MultipartFile image) {
        if (request.id() != null) {
            throw new RuntimeException("ID must be null when creating a new place type");
        }
        PlaceType placeType = PlaceType.builder()
                .name(request.name())
                .status(true)
                .build();

        placeType.setImage(s3Service.uploadFile(image, "PlaceType", "place_type_" + placeType.getId()));
        placeTypeRepository.save(placeType);
        return convertToResponse(placeType);
    }

    @Override
    public PlaceTypeResponse updatePlaceType(PlaceTypeRequest request, MultipartFile image) {
        if (request.id() == null) {
            throw new RuntimeException("ID is required for update");
        }
        PlaceType placeType = placeTypeRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Place type not found"));
        placeType.setName(request.name());
        if (image != null && !image.isEmpty()) {
            String filename = image.getOriginalFilename();
            placeType.setImage(filename);
        }
        placeType.setImage(s3Service.uploadFile(image, "PlaceType", "place_type_" + placeType.getId()));
        placeTypeRepository.save(placeType);
        return convertToResponse(placeType);
    }

    @Override
    public PagingResponse<?> getPlaceTypes(Map<String, String> params, int page, int size) {
        Specification<PlaceType> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                        break;
                    case "status":
                        predicates.add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(value)));
                        break;
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size);
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
    public MappingJacksonValue getFilteredPlaceTypes(Map<String, String> params, int page, int size, String fields) {
        PagingResponse<?> placeTypes = getPlaceTypes(params, page, size);

        SimpleFilterProvider filters = new SimpleFilterProvider()
                .addFilter("dynamicFilter", fields != null && !fields.isEmpty() ?
                        SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",")) :
                        SimpleBeanPropertyFilter.serializeAll());

        MappingJacksonValue mapping = new MappingJacksonValue(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .data(placeTypes)
                .message("Retrieve all place types successfully")
                .build());
        mapping.setFilters(filters);

        return mapping;
    }

        @Override
    public PlaceTypeResponse deletePlaceType(Integer id) {
        PlaceType placeType = placeTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Place type not found"));
        placeType.setStatus(false);
        placeTypeRepository.save(placeType);
        return convertToResponse(placeType);
    }

    private PlaceTypeResponse convertToResponse(PlaceType placeType) {
        return PlaceTypeResponse.builder()
                .id(placeType.getId())
                .name(placeType.getName())
                .imagePath(placeType.getImage())
                .status(placeType.isStatus())
                .build();
    }
}
