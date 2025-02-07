package com.group2.glamping.service.interfaces;

import com.group2.glamping.model.dto.response.UserResponseDTO;
import com.group2.glamping.model.entity.User;

import java.util.Optional;

public interface UserService {

    UserResponseDTO getUserById(int id);

    Optional<User> getUserByEmail(String email);
}
