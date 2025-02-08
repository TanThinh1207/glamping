package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.SelectionResponse;

import java.util.List;

public interface SelectionService {
    SelectionResponse createOrUpdateSelection(SelectionRequest request);

    List<SelectionResponse> getAllSelections();

    List<SelectionResponse> getSelectionsByName(String name);

    void softDeleteSelection(int id);
}
