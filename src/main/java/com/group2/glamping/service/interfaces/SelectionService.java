package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.requests.SelectionRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.SelectionResponse;

import java.util.Map;

public interface SelectionService {
    SelectionResponse createSelection(SelectionRequest request);

    SelectionResponse updateSelection(SelectionRequest request);

    PagingResponse<?> getSelections(Map<String, String> params, int page, int size);

    Object getFilteredSelections(Map<String, String> params, int page, int size, String fields);

    SelectionResponse softDeleteSelection(int id);
}
