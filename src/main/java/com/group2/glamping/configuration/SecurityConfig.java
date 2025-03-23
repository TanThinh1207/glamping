package com.group2.glamping.configuration;


import com.group2.glamping.auth.JwtAuthenticationFilter;
import com.group2.glamping.auth.google.CustomAccessDeniedHandler;
import com.group2.glamping.auth.google.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private static final String[] WHITE_LIST = {
            "/api/v1/auth/verify",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/v3/api-docs/swagger-config",
            "/api/docs/swagger",
            "/error",
            "/home",
            "/login",
            "/api/s3/image/**"
    };


    private static final String[] USER_LIST = {
            "/api/bookings/**",
            "/api/fcm-tokens/**",
            "/api/payments/**",
            "/api/reports/**",
            "/api/campsites/**",
            "/api/camp-types/**",
            "/api/selections/**"
    };

    private static final String[] MANAGER_LIST = {
            "/api/bookings/**",
            "/api/campsites/**",
            "/api/reports/**",
            "/api/payments/**"
    };

    private static final String[] ADMIN_LIST = {
            "/api/users/**",
            "/api/facilities/**",
            "/api/utilities/**",
            "/api/place-types/**",
            "/api/demo/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll()
//                        .requestMatchers(WHITE_LIST).permitAll()
//                        .requestMatchers(USER_LIST).hasRole("USER")
//                        .requestMatchers(MANAGER_LIST).hasRole("MANAGER")
//                        .requestMatchers(ADMIN_LIST).hasRole("ADMIN")
//                        .anyRequest().authenticated()
                )
                .securityContext(security -> security.requireExplicitSave(true))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new DefaultOAuth2UserService();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
