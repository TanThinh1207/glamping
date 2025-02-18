package com.group2.glamping.service.impl;


import com.group2.glamping.auth.JwtService;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.RegisterRequest;
import com.group2.glamping.model.dto.response.AuthenticationResponse;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.Role;
import com.group2.glamping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    //private final AuthenticationManager authenticationManager;

    public AuthenticationResponse verify(RegisterRequest request) throws AppException {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent() && userOptional.get().isStatus()) {
            var jwtToken = jwtService.generateToken(userOptional.get());
            return AuthenticationResponse.builder()
                    .isNew(false)
                    .message("Old user")
                    .accessToken(jwtToken)
                    .build();
        } else if (!userOptional.get().isStatus()) {
            throw new AppException(ErrorCode.USER_NOT_AVAILABLE);
        }

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(true)
                .role(Role.ROLE_USER)
                .address(request.getAddress())
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .phoneNumber(request.getPhone())
                .createdTime(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .message("New user")
                .isNew(true)
                .accessToken(jwtToken)
                .build();
    }




//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//
//        var user = userRepository
//                .findByEmail(request.getEmail())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
//
//        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);
//
//        var jwtToken = jwtService.generateToken(user);
//
//        return AuthenticationResponse.builder()
//                .user(user)
//                .message("いいですね")
//                .accessToken(jwtToken)
//                .build();
//    }


}
