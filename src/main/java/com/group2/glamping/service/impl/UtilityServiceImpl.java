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
    public UtilityResponse createUtility(UtilityRequest request) {
        UtilityResponse.builder()
                .name(request.name())
//                .imagePath(request.imagePath() != null ? request.imagePath().getOriginalFilename() : null)
                .status(true)
                .build();
        Utility utility = mapRequestToEntity(request);
        utility.setStatus(true);
        utilityRepository.save(utility);
        return mapEntityToResponse(utility);
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

        return mapEntityToResponse(existingUtility);
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

    //Get Utilities by status
    @Override
    public List<UtilityResponse> getUtilitiesByStatus(Boolean status) {
        List<Utility> utilities = utilityRepository.findByStatus(status);
        return utilities.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UtilityResponse softDeleteUtility(int id) {
        Utility utility = getUtilityById(id);
        utility.setStatus(false);
        utilityRepository.save(utility);
        return mapEntityToResponse(utility);
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
    private UtilityResponse mapEntityToResponse(Utility utility) {
        return UtilityResponse.builder()
                .id(utility.getId())
                .name(utility.getName())
                .imagePath(utility.getImageUrl())
                .status(utility.isStatus())
                .build();
    }
}
