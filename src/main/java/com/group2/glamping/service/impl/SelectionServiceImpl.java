package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.SelectionResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.Selection;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.SelectionRepository;
import com.group2.glamping.service.interfaces.S3Service;
import com.group2.glamping.service.interfaces.SelectionService;
import com.group2.glamping.utils.ResponseFilterUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SelectionServiceImpl implements SelectionService {

    private final SelectionRepository selectionRepository;
    private final CampSiteRepository campSiteRepository;
    private final S3Service s3Service;

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
        return convertToResponse(selection);
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
        return convertToResponse(selection);
    }

    @Override
    public PagingResponse<?> getSelections(Map<String, String> params, int page, int size) {
        Specification<Selection> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((key, value) -> {
                switch (key) {
                    case "name" -> predicates.add(criteriaBuilder.like(root.get("name"), "%" + value + "%"));
                    case "status" ->
                            predicates.add(criteriaBuilder.equal(root.get("status"), Boolean.parseBoolean(value)));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size);
        Page<Selection> selectionPage = selectionRepository.findAll(spec, pageable);
        List<SelectionResponse> selectionResponses = selectionPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return new PagingResponse<>(
                selectionResponses,
                selectionPage.getTotalElements(),
                selectionPage.getTotalPages(),
                selectionPage.getNumber(),
                selectionPage.getNumberOfElements()
        );
    }

    @Override
    public Object getFilteredSelections(Map<String, String> params, int page, int size, String fields) {
        PagingResponse<?> selections = getSelections(params, page, size);
        return ResponseFilterUtil.getFilteredResponse(fields, selections, "Retrieve filtered list successfully");
    }


    //Soft delete
    @Override
    public SelectionResponse softDeleteSelection(int id) {
        Selection selection = selectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Selection not found with id: " + id));
        selection.setStatus(false);
        selectionRepository.save(selection);
        return convertToResponse(selection);
    }

    // Mapping entity Selection to SelectionResponse
    private SelectionResponse convertToResponse(Selection selection) {
        SelectionResponse response = new SelectionResponse();
        response.setId(selection.getId());
        response.setName(selection.getName());
        response.setDescription(selection.getDescription());
        response.setPrice(selection.getPrice());
        response.setImage(s3Service.generatePresignedUrl(selection.getImageUrl()));
        response.setStatus(selection.isStatus());
        return response;
    }
}
