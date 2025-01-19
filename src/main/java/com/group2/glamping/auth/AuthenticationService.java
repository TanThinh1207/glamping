package com.group2.glamping.auth;


import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.AuthenticationRequest;
import com.group2.glamping.model.dto.requests.RegisterRequest;
import com.group2.glamping.model.dto.response.AuthenticationResponse;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.model.enums.Role;
import com.group2.glamping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    //private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) throws AppException {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_EXISTED);
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
                .build();

        user = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        var user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .message("いいですね")
                .accessToken(jwtToken)
                .build();
    }


}
