package com.group2.glamping.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.model.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;

public class ResponseFilterUtil {

    public static Object getFilteredResponse(String fields, Object data, String message) {
        // Tạo BaseResponse với statusCode và message
        BaseResponse response = BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();

        // Nếu có fields, lọc các trường dữ liệu
        if (fields != null && !fields.isEmpty()) {
            try {
                // Lọc dữ liệu theo các trường được yêu cầu
                ObjectMapper mapper = new ObjectMapper();
                SimpleFilterProvider filters = new SimpleFilterProvider()
                        .addFilter("dynamicFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields.split(",")));

                mapper.setFilterProvider(filters);

                // Áp dụng filter và chuyển đổi dữ liệu thành đối tượng đã được lọc
                response.setData(mapper.convertValue(data, Object.class));

            } catch (Exception e) {
                throw new RuntimeException("Failed to filter response fields", e);
            }
        }

        return response;
    }

}
