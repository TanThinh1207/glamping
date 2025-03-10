package com.group2.glamping.utils;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.model.dto.response.PagingResponse;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.util.List;

public class ResponseFilterUtil {

    public static <T> PagingResponse<T> getFilteredResponse(String fields, PagingResponse<T> pagingResponse) {
        if (fields != null && !fields.isEmpty()) {
            try {
                String[] fieldArray = fields.split(",");

                SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                        .addFilter("dynamicFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fieldArray))
                        .setFailOnUnknownId(false);

                // Wrap the content of the PagingResponse in a MappingJacksonValue
                MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(pagingResponse.getContent());
                mappingJacksonValue.setFilters(filterProvider);

                // Update the content of the PagingResponse with the filtered data
                pagingResponse.setContent((List<T>) mappingJacksonValue.getValue());

                return pagingResponse;
            } catch (Exception e) {
                throw new RuntimeException("Failed to filter response fields", e);
            }
        }

        return pagingResponse;
    }
}