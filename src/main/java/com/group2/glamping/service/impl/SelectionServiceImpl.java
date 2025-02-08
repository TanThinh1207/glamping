package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.SelectionResponse;
import com.group2.glamping.model.entity.CampSite;
import com.group2.glamping.model.entity.CampSiteSelection;
import com.group2.glamping.model.entity.Selection;
import com.group2.glamping.model.entity.id.IdCampSiteSelection;
import com.group2.glamping.repository.CampSiteRepository;
import com.group2.glamping.repository.SelectionRepository;
import com.group2.glamping.service.interfaces.SelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
//            selection.setStatus(true);
        }

        selection.setName(request.name());
        selection.setDescription(request.description());
        selection.setPrice(request.price());

        if (request.image() != null && !request.image().isEmpty()) {
            String filename = request.image().getOriginalFilename();
            selection.setImageUrl(filename);
        }

        selection.setUpdatedTime(LocalDateTime.now());

        // Nếu client gửi kèm idCampSite, xử lý quan hệ giữa Selection và CampSite
        if (request.campSiteId() != null) {
            // Tìm CampSite theo id
            CampSite campSite = campSiteRepository.findById(request.id())
                    .orElseThrow(() -> new RuntimeException("CampSite not found with id: " + request.id()));


            CampSiteSelection campSiteSelection = new CampSiteSelection();

            if (selection.getId() == 0) {
                selection = selectionRepository.save(selection);
            }

            // Khởi tạo IdCampSiteSelection (giả sử có constructor phù hợp)
//            IdCampSiteSelection idComposite = new IdCampSiteSelection(campSite.getId(), selection.getId());
//            campSiteSelection.setCampSiteSelectionId(idComposite);
            campSiteSelection.setCampSite(campSite);
            campSiteSelection.setSelection(selection);

            if (selection.getCampSiteSelectionList() == null) {
                selection.setCampSiteSelectionList(new ArrayList<>());
            }

            selection.getCampSiteSelectionList().add(campSiteSelection);
        }

        // Lưu Selection (và cascade sẽ lưu cả CampSiteSelection nếu cấu hình cascade)
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
        // Chuyển trạng thái của Selection về false (soft delete)
        selection.setStatus(false);
        selectionRepository.save(selection);
    }

    // Phương thức mapping từ entity Selection sang SelectionResponse
    private SelectionResponse mapEntityToResponse(Selection selection) {
        SelectionResponse response = new SelectionResponse();
        response.setId(selection.getId());
        response.setName(selection.getName());
        response.setDescription(selection.getDescription());
        response.setPrice(selection.getPrice());
//        response.setIdCampSite(selection.getIdCampSite());
//        response.setImage(selection.getImage());
//        response.setStatus(selection.getStatus());
        return response;
    }

}
