package com.group2.glamping.filter;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;

public class FilterMappingJacksonValue<T> extends MappingJacksonValue {

    public FilterMappingJacksonValue(final T value, final String... filters) {
        super(value);
        setFilters(new SimpleFilterProvider().addFilter("dynamicFilter",
                filters.length > 0 ? SimpleBeanPropertyFilter.filterOutAllExcept(filters) : SimpleBeanPropertyFilter.serializeAll()));
    }
}