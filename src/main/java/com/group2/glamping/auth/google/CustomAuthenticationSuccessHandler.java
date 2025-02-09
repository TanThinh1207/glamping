package com.group2.glamping.auth.google;

import com.group2.glamping.auth.JwtService;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.response.AuthenticationResponse;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.Role;
import com.group2.glamping.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtTokenProvider;
    private final UserRepository userRepository;
    private final GoogleCallbackConfig googleCallbackConfig;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            throw new AppException(ErrorCode.INACTIVE_USER);
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);

        AuthenticationResponse authenticationResponse;


        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.isStatus()) {
                String token = jwtTokenProvider.generateToken(user);
                authenticationResponse = AuthenticationResponse.builder()
                        .user(user)
                        .accessToken(token)
                        .build();

            } else {
                throw new AppException(ErrorCode.INACTIVE_USER);
            }
        } else {
            User newUser = User.builder()
                    .email(email)
                    .status(true)
                    .created_at(LocalDateTime.now())
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(newUser);
            String token = jwtTokenProvider.generateToken(newUser);
            authenticationResponse = AuthenticationResponse.builder()
                    .user(newUser)
                    .accessToken(token)
                    .build();
        }

//        response.setContentType("application/json");

//        objectMapper.writeValue(response.getWriter(), authenticationResponse);

        if (!response.isCommitted()) {
            String redirectUrl = googleCallbackConfig.getGoogleCallbackUrl() + "?token=" + authenticationResponse.getAccessToken();
            response.sendRedirect(redirectUrl);
        }


    }
}
