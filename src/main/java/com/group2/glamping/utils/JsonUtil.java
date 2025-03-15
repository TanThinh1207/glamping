package com.group2.glamping.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.group2.glamping.model.dto.response.CampSiteResponse;
import com.group2.glamping.model.dto.response.PagingResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SimpleModule()
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
            .registerModule(new JavaTimeModule())
            .setFilterProvider(new SimpleFilterProvider()
                    .addFilter("dynamicFilter", SimpleBeanPropertyFilter.serializeAll()) // Thêm filter cụ thể
                    .setDefaultFilter(SimpleBeanPropertyFilter.serializeAll()));


    public static PagingResponse<CampSiteResponse> deserializePagingResponse(String data) throws JsonProcessingException {
//        System.out.println("Raw JSON data: " + data); // See the raw cached response
        try {
            return objectMapper.readValue(data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            System.err.println("Failed to deserialize: " + e.getMessage());
            throw e;
        }
    }

    public static String serializePagingResponse(PagingResponse<?> response) {
        try {
            System.out.println("JsonUtil serializePagingResponse - Response: " + response);
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize PagingResponse", e);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }


}
