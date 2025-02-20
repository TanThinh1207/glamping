package com.group2.glamping.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register Java 8 Date/Time module
        mapper.registerModule(new JavaTimeModule());

        // Enable filtering dynamically
        mapper.setFilterProvider(new SimpleFilterProvider().addFilter(
                "dynamicFilter", SimpleBeanPropertyFilter.serializeAll()));

        return mapper;
    }
}
