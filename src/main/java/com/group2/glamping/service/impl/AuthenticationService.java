package com.group2.glamping.service.impl;


import com.group2.glamping.auth.JwtService;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.response.AuthenticationResponse;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse verify(String email) throws AppException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!user.isStatus()) {
                throw new AppException(ErrorCode.USER_NOT_AVAILABLE);
            }

            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .message("Old user")
                    .isNew(false)
                    .accessToken(jwtToken)
                    .build();
        }


        User newUser = User.builder()
                .email(email)
//                .password(passwordEncoder.encode(email))
                .status(true)
                .build();

        userRepository.save(newUser); // Lưu user mới vào database
        String jwtToken = jwtService.generateToken(newUser);

        return AuthenticationResponse.builder()
                .isNew(true)
                .message("New user")
                .accessToken(jwtToken)
                .build();
    }


}




