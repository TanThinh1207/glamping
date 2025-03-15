package com.group2.glamping.filter;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.group2.glamping.model.dto.response.PagingResponse;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ControllerAdvice
@SuppressWarnings("unused")
public class FilterAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public Object beforeBodyWrite(Object body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType,
                                  @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        if (!(request instanceof ServletServerHttpRequest)) {
            return body;
        }

        String fields = ((ServletServerHttpRequest) request).getServletRequest().getParameter("fields");

        if (StringUtils.isEmpty(fields)) {
            return body;
        }

        Set<String> fieldSet = Stream.of(fields.split(",")).collect(Collectors.toSet());

        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("dynamicFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fieldSet));

        MappingJacksonValue mapping = new MappingJacksonValue(body);

        if (body instanceof PagingResponse<?> pagingResponse) {
            List<?> originalContent = pagingResponse.getContent();
            List<Object> filteredContent = originalContent.stream()
                    .map(item -> {
                        MappingJacksonValue itemMapping = new MappingJacksonValue(item);
                        itemMapping.setFilters(filterProvider);
                        return itemMapping.getValue();
                    })
                    .toList();

            setContentSafely(pagingResponse, filteredContent);
        }

        mapping.setFilters(filterProvider);
        return mapping;
    }

    @SuppressWarnings("unchecked")
    private <T> void setContentSafely(PagingResponse<T> pagingResponse, List<?> filteredContent) {
        List<T> safeList = filteredContent.stream()
                .map(item -> (T) item) // Ép kiểu từng phần tử
                .toList(); // Chuyển về List<T>

        pagingResponse.setContent(safeList);
    }


    @Override
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}
