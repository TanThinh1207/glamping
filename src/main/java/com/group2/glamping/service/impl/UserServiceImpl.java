package com.group2.glamping.service.impl;

import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.UserUpdateRequest;
import com.group2.glamping.model.dto.response.UserResponse;
import com.group2.glamping.model.entity.User;
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
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().filter(User::isStatus).map(UserResponse::new).collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(int id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setFirstname(userUpdateRequest.firstName());
        user.setLastname(userUpdateRequest.lastName());
        user.setPassword(userUpdateRequest.phone());
        user.setDob(userUpdateRequest.dob());
        user.setStatus(userUpdateRequest.status());
        user.setAddress(userUpdateRequest.address());

        userRepository.save(user);
        return new UserResponse(user);
    }

    @Override
    public UserResponse deleteUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(false);
        userRepository.save(user);

        return new UserResponse(user);
    }

}
