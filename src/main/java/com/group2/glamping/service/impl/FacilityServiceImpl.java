package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.FacilityRequest;
import com.group2.glamping.model.dto.response.FacilityResponse;
import com.group2.glamping.model.entity.Facility;
import com.group2.glamping.repository.FacilityRepository;
import com.group2.glamping.service.interfaces.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;

    @Override
    public FacilityResponse createFacility(FacilityRequest request) {
        if (request.id() != null) {
            throw new RuntimeException("ID must be null when creating a new facility");
        }
        Facility facility = new Facility();
        facility.setName(request.name());
        facility.setDescription(request.description());
        if (request.image() != null && !request.image().isEmpty()) {
            facility.setImageUrl(request.image().getOriginalFilename());
        }
        facility.setStatus(true);
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
    public List<FacilityResponse> getFacilityByName(String name) {
        List<Facility> facilities = facilityRepository.findByNameContainingIgnoreCase(name);
        if (facilities.isEmpty()) {
            throw new RuntimeException("No facilities found with name: " + name);
        }
        return facilities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FacilityResponse> getFacilitiesByStatus(Boolean status) {
        // Nếu status null, có thể chọn trả về tất cả hoặc ném exception – ở đây mình giả sử trả về tất cả
        if (status == null) {
            return getAllFacilities();
        }
        List<Facility> facilities = facilityRepository.findByStatus(status);
        if (facilities.isEmpty()) {
            throw new RuntimeException("No facilities found with status: " + status);
        }
        return facilities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
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
        return new FacilityResponse(
                facility.getId(),
                facility.getName(),
                facility.getDescription(),
                facility.getImageUrl(),
                facility.isStatus()
        );
    }
}
