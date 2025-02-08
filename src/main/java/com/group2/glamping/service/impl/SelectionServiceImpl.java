package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.SelectionResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.Selection;
import com.group2.glamping.model.mapper.CampSiteMapper;
import com.group2.glamping.model.mapper.CampSiteMapper2;
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

    @Override
    public SelectionResponse createOrUpdateSelection(SelectionRequest request) {
        Selection selection;
        if (request.id() != null) {
            selection = selectionRepository.findById(request.id())
                    .orElse(new Selection());
        } else {
            selection = new Selection();
            selection.setStatus(true);
        }

        selection.setName(request.name());
        selection.setDescription(request.description());
        selection.setPrice(request.price());

        if (request.image() != null && !request.image().isEmpty()) {
            String filename = request.image().getOriginalFilename();
            selection.setImageUrl(filename);
        }

        selection.setUpdatedTime(LocalDateTime.now());

        //Handle campsite
        if (request.campSiteId() != null) {
            CampSite campSite = campSiteRepository.findById(request.campSiteId())
                    .orElseThrow(() -> new RuntimeException("CampSite not found with id: " + request.campSiteId()));
            selection.setCampSite(campSite);
        }

        selection = selectionRepository.save(selection);

        return mapEntityToResponse(selection);
    }

    @Override
    public List<SelectionResponse> getAllSelections() {
        List<Selection> selections = selectionRepository.findAll();
        return selections.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SelectionResponse> getSelectionsByName(String name) {
        List<Selection> selections = selectionRepository.findByNameContainingIgnoreCase(name);
        return selections.stream()
                .map(this::mapEntityToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void softDeleteSelection(int id) {
        Selection selection = selectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Selection not found with id: " + id));
        selection.setStatus(false);
        selectionRepository.save(selection);
    }

    // Mapping entity Selection to SelectionResponse
    private SelectionResponse mapEntityToResponse(Selection selection) {
        SelectionResponse response = new SelectionResponse();
        response.setId(selection.getId());
        response.setName(selection.getName());
        response.setDescription(selection.getDescription());
        response.setPrice(selection.getPrice());
        response.setImage(selection.getImageUrl());

        // Kiểm tra nếu campSite không null trước khi ánh xạ
        CampSite campSite = selection.getCampSite();
        if (campSite != null) {
            response.setCampSiteResponse(CampSiteMapper2.toDto(selection.getCampSite(), true));
        } else {
            response.setCampSiteResponse(null);
        }

        return response;
    }
}
