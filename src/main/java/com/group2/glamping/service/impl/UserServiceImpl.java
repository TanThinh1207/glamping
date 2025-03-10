package com.group2.glamping.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.UserUpdateRequest;
import com.group2.glamping.model.dto.response.UserResponse;
import com.group2.glamping.model.entity.FcmToken;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.repository.FcmTokenRepository;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final FcmTokenRepository fcmTokenRepository;


    @Override
    public UserResponse getUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return new UserResponse(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.of(userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().filter(User::isStatus).map(UserResponse::new).collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(int id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setFirstname(userUpdateRequest.firstName());
        user.setLastname(userUpdateRequest.lastName());
        user.setPhoneNumber(userUpdateRequest.phone());
//        user.setPassword(userUpdateRequest.phone());
        user.setDob(userUpdateRequest.dob());
        user.setStatus(userUpdateRequest.status());
        user.setAddress(userUpdateRequest.address());

        userRepository.save(user);
        return new UserResponse(user);
    }

    @Override
    public UserResponse deleteUser(int id) throws FirebaseAuthException {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(false);
        userRepository.save(user);
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(user.getEmail());
        UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(userRecord.getUid()).setDisabled(true);
        FirebaseAuth.getInstance().updateUser(updateRequest);
        return new UserResponse(user);
    }

    @Override
    public String updateFcmToken(int userId, String fcmToken) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!fcmTokenRepository.existsByTokenAndUserId(fcmToken, userId)) {
                FcmToken token = new FcmToken();
                token.setToken(fcmToken);
                token.setUser(user);
                fcmTokenRepository.save(token);
            }
            return "FCM Token added successfully";
        }
        return "User not found";
    }

    @Override
    public String removeFcmToken(int userId, String fcmToken) {
        if (fcmTokenRepository.existsByTokenAndUserId(fcmToken, userId)) {
            fcmTokenRepository.deleteByTokenAndUserId(fcmToken, userId);
            return "FCM Token removed successfully";
        }
        return "FCM Token not found";
    }

}
