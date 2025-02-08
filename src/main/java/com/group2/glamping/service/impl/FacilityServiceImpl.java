package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.entity.Facility;
import com.group2.glamping.repository.FacilityRepository;
import com.group2.glamping.service.interfaces.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;

    @Override
    public FacilityResponse createOrUpdateFacility(FacilityRequest request) {
        Facility facility = request.id() == null
                ? new Facility()
                : facilityRepository.findById(request.id()).orElseThrow(() -> new RuntimeException("Facility not found"));

        facility.setName(request.name());
        facility.setDescription(request.description());

        // Lưu tên file ảnh (sau này sẽ đổi thành đường dẫn S3)
        if (request.image() != null && !request.image().isEmpty()) {
            facility.setImageUrl(request.image().getOriginalFilename());
        }

        facilityRepository.save(facility);
        return convertToResponse(facility);
    }

    @Override
    public List<FacilityResponse> getAllFacilities() {
        return facilityRepository.findAll()
                .stream().map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FacilityResponse getFacilityByName(String name) {
        Facility facility = facilityRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        return convertToResponse(facility);
    }

    @Override
    public Void deleteFacility(Integer id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        facility.setStatus(false);
        facilityRepository.save(facility);
        return null;
    }

    private FacilityResponse convertToResponse(Facility facility) {
        return new FacilityResponse(
                facility.getId(),
                facility.getName(),
                facility.getDescription(),
                facility.getImageUrl()
        );
    }
}
