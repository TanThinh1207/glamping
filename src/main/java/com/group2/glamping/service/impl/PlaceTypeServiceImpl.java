package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.PlaceTypeRequest;
import com.group2.glamping.model.dto.response.PlaceTypeResponse;
import com.group2.glamping.model.entity.PlaceType;
import com.group2.glamping.repository.PlaceTypeRepository;
import com.group2.glamping.service.interfaces.PlaceTypeService;
import com.group2.glamping.service.interfaces.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<PlaceTypeResponse> getAllPlaceTypes() {
        return placeTypeRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaceTypeResponse> getPlaceTypeByName(String name) {
        List<PlaceType> placeTypes = placeTypeRepository.findByNameContainingIgnoreCase(name);
        if (placeTypes.isEmpty()) {
            return Collections.emptyList();
        }
        return placeTypes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaceTypeResponse> getPlaceTypeByStatus(Boolean status) {
        if (status == null) {
            return getAllPlaceTypes();
        }
        List<PlaceType> placeTypes = placeTypeRepository.findByStatus(status);

        //If placeType list is empty -> return empty list
        if (placeTypes.isEmpty()) {
            return Collections.emptyList();
        }

        return placeTypes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
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
