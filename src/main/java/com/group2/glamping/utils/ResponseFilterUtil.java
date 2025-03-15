package com.group2.glamping.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.model.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;

public class ResponseFilterUtil {

    public static Object getFilteredResponse(String fields, Object data, String message) {
        BaseResponse response = BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .data(data)
                .message(message)
                .build();

        if (fields != null && !fields.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                SimpleFilterProvider filters = new SimpleFilterProvider()
                        .addFilter("dynamicFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",")));

                mapper.setFilterProvider(filters);

                return mapper.convertValue(response, Object.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to filter response fields", e);
            }
        }

        return response;
    }

}
