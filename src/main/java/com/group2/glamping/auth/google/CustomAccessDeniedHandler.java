package com.group2.glamping.auth.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.glamping.model.dto.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        BaseResponse errorResponse = BaseResponse.builder()
                .statusCode(HttpServletResponse.SC_FORBIDDEN)
                .message("Forbidden - You do not have permission to access this resource")
                .data(null)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
