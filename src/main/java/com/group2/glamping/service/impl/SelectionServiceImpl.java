package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.SelectionResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.Selection;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.SelectionRepository;
import com.group2.glamping.service.interfaces.SelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelectionServiceImpl implements SelectionService {

    private final SelectionRepository selectionRepository;
    private final CampSiteRepository campSiteRepository;

    // Create selection
    @Override
    public SelectionResponse createSelection(SelectionRequest request) {
        if (request.id() != null) {
            throw new RuntimeException("ID must be null for creation");
        }
        Selection selection = new Selection();
        selection.setStatus(true);
        selection.setName(request.name());
        selection.setDescription(request.description());
        selection.setPrice(request.price());
//        if (request.image() != null && !request.image().isEmpty()) {
//            String filename = request.image().getOriginalFilename();
//            selection.setImageUrl(filename);
//        }
        selection.setUpdatedTime(LocalDateTime.now());
        if (request.campSiteId() != null) {
            CampSite campSite = campSiteRepository.findById(request.campSiteId())
                    .orElseThrow(() -> new RuntimeException("CampSite not found with id: " + request.campSiteId()));
            selection.setCampSite(campSite);
        }
        selection = selectionRepository.save(selection);
        return mapEntityToResponse(selection);
    }

    // Update selection
    @Override
    public SelectionResponse updateSelection(SelectionRequest request) {
        if (request.id() == null) {
            throw new RuntimeException("ID is required for update");
        }
        Selection selection = selectionRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Selection not found with id: " + request.id()));
        if (request.name() != null) {
            selection.setName(request.name());
        }
        if (request.description() != null) {
            selection.setDescription(request.description());
        }
//        selection.setPrice(request.price());
//        if (request.image() != null && !request.image().isEmpty()) {
//            String filename = request.image().getOriginalFilename();
//            selection.setImageUrl(filename);
//        }
        selection.setUpdatedTime(LocalDateTime.now());
        if (request.campSiteId() != null) {
            CampSite campSite = campSiteRepository.findById(request.campSiteId())
                    .orElseThrow(() -> new RuntimeException("CampSite not found with id: " + request.campSiteId()));
            selection.setCampSite(campSite);
        }
        selection = selectionRepository.save(selection);
        return mapEntityToResponse(selection);
    }

    //Get all selection
    @Override
    public List<SelectionResponse> getAllSelections() {
        List<Selection> selections = selectionRepository.findAll();
        return selections.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    //Get selection by name ignoreCase
    @Override
    public List<SelectionResponse> getSelectionsByName(String name) {
        List<Selection> selections = selectionRepository.findByNameContainingIgnoreCase(name);
        return selections.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    //Get selection by status
    @Override
    public List<SelectionResponse> getSelectionsByStatus(boolean status) {
        List<Selection> selections = selectionRepository.findByStatus(status);
        return selections.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    //Soft delete
    @Override
    public SelectionResponse softDeleteSelection(int id) {
        Selection selection = selectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Selection not found with id: " + id));
        selection.setStatus(false);
        selectionRepository.save(selection);
        return mapEntityToResponse(selection);
    }

    // Mapping entity Selection to SelectionResponse
    private SelectionResponse mapEntityToResponse(Selection selection) {
        SelectionResponse response = new SelectionResponse();
        response.setId(selection.getId());
        response.setName(selection.getName());
        response.setDescription(selection.getDescription());
        response.setPrice(selection.getPrice());
        response.setImage(selection.getImageUrl());
        response.setStatus(selection.isStatus());
        return response;
    }
}
