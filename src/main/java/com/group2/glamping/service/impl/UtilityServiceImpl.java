package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.UtilityRequest;
import com.group2.glamping.model.dto.response.UtilityResponse;
import com.group2.glamping.model.entity.Utility;
import com.group2.glamping.repository.UtilityRepository;
import com.group2.glamping.service.interfaces.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilityServiceImpl implements UtilityService {

    private final UtilityRepository utilityRepository;

    @Override
    public UtilityResponse saveOrUpdateUtility(UtilityRequest request) {
        // Mapping request to entity
        Utility utility = mapRequestToEntity(request);

        utility = utilityRepository.save(utility);

        // Mapping to utility entity and return
        return mapEntityToResponse(utility);
    }

    @Override
    public Utility getUtilityById(int id) {
        return utilityRepository.findById(id).orElseThrow(() -> new RuntimeException("Utility not found"));
    }

    @Override
    public List<UtilityResponse> getAllUtilities() {
        List<Utility> utilities = utilityRepository.findAll();
        return utilities.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UtilityResponse> getUtilitiesByName(String name) {
        List<Utility> utilities = utilityRepository.findByNameContainingIgnoreCase(name);
        return utilities.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void softDeleteUtility(int id) {
        Utility utility = getUtilityById(id);
        utility.setStatus(false);
        utilityRepository.save(utility);
    }

    // Mapping request to entity
    private Utility mapRequestToEntity(UtilityRequest request) {
        Utility utility;

        if (request.id() != null) {
            utility = utilityRepository.findById(request.id()).orElse(new Utility());
        } else {
            utility = new Utility();
        }

        utility.setName(request.name());

        if (request.imagePath() != null && !request.imagePath().isEmpty()) {
            String filename = request.imagePath().getOriginalFilename();
            utility.setImageUrl(filename);
        } else if (request.id() != null) {  // Nếu không có ảnh mới, giữ ảnh cũ
            Utility existingUtility = getUtilityById(request.id());
            utility.setImageUrl(existingUtility.getImageUrl());
        }

        return utility;
    }


    // Mapping to entity to entityResponse
    private UtilityResponse mapEntityToResponse(Utility utility) {
        UtilityResponse response = new UtilityResponse();
        response.setId(utility.getId());
        response.setName(utility.getName());
        response.setImagePath(utility.getImageUrl());
        return response;
    }
}
