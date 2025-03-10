package com.group2.glamping.service.interfaces;

import com.google.firebase.auth.FirebaseAuthException;
import com.group2.glamping.model.dto.requests.UserUpdateRequest;
import com.group2.glamping.model.dto.response.UserResponse;
import com.group2.glamping.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserResponse getUserById(int id);

    Optional<User> getUserByEmail(String email);

    List<UserResponse> getUsers();

    UserResponse updateUser(int id, UserUpdateRequest userUpdateRequest);

    UserResponse deleteUser(int id) throws FirebaseAuthException;

    String updateFcmToken(int userId, String fcmToken);

    String removeFcmToken(int userId, String fcmToken);
}
