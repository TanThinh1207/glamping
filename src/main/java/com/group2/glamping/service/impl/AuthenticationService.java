package com.group2.glamping.service.impl;


import com.group2.glamping.auth.JwtService;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.VerifyTokenRequest;
import com.group2.glamping.model.dto.response.AuthenticationResponse;
import com.group2.glamping.model.dto.response.UserResponse;
import com.group2.glamping.model.entity.FcmToken;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.Role;
import com.group2.glamping.repository.FcmTokenRepository;
import com.group2.glamping.repository.UserRepository;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final StripeService stripeService;
    private final FcmTokenRepository fcmTokenRepository;


    public AuthenticationResponse verify(VerifyTokenRequest request, String email) throws AppException, StripeException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!fcmTokenRepository.existsByDeviceIdAndUserId(request.deviceId(), user.getId())) {
                fcmTokenRepository.save(new FcmToken(0, request.fcmToken(), user, request.deviceId()));
            }
            if (!user.isStatus()) {
                throw new AppException(ErrorCode.USER_NOT_AVAILABLE);
            }

            String jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .message("Old user")
                    .isNew(false)
                    .user(new UserResponse(user))
                    .accessToken(jwtToken)
                    .build();
        }

        User newUser = User.builder()
                .email(email)
                .role(Role.ROLE_USER)
                .createdTime(LocalDateTime.now())
                .status(true)
                .build();

        userRepository.save(newUser);
        String jwtToken = jwtService.generateToken(newUser);
        stripeService.createHostAccount(email);

        return AuthenticationResponse.builder()
                .isNew(true)
                .message("New user")
                .user(new UserResponse(newUser))
                .accessToken(jwtToken)
                .build();
    }


}

