package com.group2.glamping.service.impl;

import com.group2.glamping.model.dto.response.UserResponseDTO;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponseDTO getUserById(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponseDTO(user);
    }
}
