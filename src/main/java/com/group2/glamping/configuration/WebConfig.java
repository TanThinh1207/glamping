package com.group2.glamping.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3001")
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("Authorization") // Thêm headers cần expose
                .allowCredentials(true)
                .maxAge(3600);
    }
}