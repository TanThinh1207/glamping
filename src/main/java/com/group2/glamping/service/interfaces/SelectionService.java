package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.SelectionResponse;

import java.util.List;

public interface SelectionService {
    SelectionResponse createSelection(SelectionRequest request);

    SelectionResponse updateSelection(SelectionRequest request);

    List<SelectionResponse> getAllSelections();

    List<SelectionResponse> getSelectionsByName(String name);

    List<SelectionResponse> getSelectionsByStatus(boolean status);

    SelectionResponse softDeleteSelection(int id);
}
