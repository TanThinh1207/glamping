package com.group2.glamping.service.interfaces;

import com.google.firebase.auth.FirebaseAuthException;
import com.group2.glamping.model.dto.requests.UserUpdateRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.UserResponse;

import java.util.Map;
import java.util.Optional;

public interface UserService {


    PagingResponse<?> getUsers(Map<String, String> params, int page, int size, String sortBy, String direction);

    UserResponse updateUser(int id, UserUpdateRequest userUpdateRequest);

    UserResponse deleteUser(int id) throws FirebaseAuthException;

    String updateFcmToken(int userId, String fcmToken);

    String removeFcmToken(int userId, String fcmToken);

    Object getFilteredUsers(Map<String, String> params, int page, int size, String fields, String sortBy, String direction);

    Optional<UserResponse> getUserById(int id);

}
