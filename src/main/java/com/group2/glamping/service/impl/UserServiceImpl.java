package com.group2.glamping.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.group2.glamping.exception.AppException;
import com.group2.glamping.exception.ErrorCode;
import com.group2.glamping.model.dto.requests.UserUpdateRequest;
import com.group2.glamping.model.dto.response.PagingResponse;
import com.group2.glamping.model.dto.response.UserResponse;
import com.group2.glamping.model.entity.FcmToken;
import com.group2.glamping.model.entity.User;
import com.group2.glamping.repository.FcmTokenRepository;
import com.group2.glamping.repository.UserRepository;
import com.group2.glamping.service.interfaces.UserService;
import com.group2.glamping.utils.ResponseFilterUtil;
import com.stripe.exception.StripeException;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final FcmTokenRepository fcmTokenRepository;

    @Override
    public PagingResponse<?> getUsers(Map<String, String> params, int page, int size, String sortBy, String direction) {
        Specification<User> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Apply filtering based on params
            params.forEach((key, value) -> {
                switch (key) {
                    case "id":
                        predicates.add(criteriaBuilder.equal(root.get("id"), value));
                        break;
                    case "firstName":
                        predicates.add(criteriaBuilder.like(root.get("first_name"), "%" + value + "%"));
                        break;
                    case "lastName":
                        predicates.add(criteriaBuilder.like(root.get("last_name"), "%" + value + "%"));
                        break;
                    case "address":
                        predicates.add(criteriaBuilder.like(root.get("address"), "%" + value + "%"));
                        break;
                    case "phoneNumber":
                        predicates.add(criteriaBuilder.like(root.get("phone_number"), "%" + value + "%"));
                        break;
                    case "dob":
                        predicates.add(criteriaBuilder.like(root.get("dob"), "%" + value + "%"));
                        break;
                    case "email":
                        predicates.add(criteriaBuilder.like(root.get("email"), "%" + value + "%"));
                        break;
                    case "status":
                        predicates.add(criteriaBuilder.equal(root.get("status"), value));
                        break;
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(user -> {
                    try {
                        return new UserResponse(user);
                    } catch (Exception e) {
                        throw new RuntimeException("Lỗi khi tạo UserResponse: " + e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());


        return new PagingResponse<>(
                userResponses,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.getNumber(),
                userPage.getNumberOfElements()
        );
    }


    @Override
    public UserResponse updateUser(int id, UserUpdateRequest userUpdateRequest) throws StripeException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        User updatedUser = user.toBuilder()
                .firstname(userUpdateRequest.firstName())
                .lastname(userUpdateRequest.lastName())
                .phoneNumber(userUpdateRequest.phone())
                .dob(userUpdateRequest.dob())
                .address(userUpdateRequest.address())
                .build();

        if (userUpdateRequest.status() != null) {
            updatedUser.setStatus(userUpdateRequest.status());
        }

        userRepository.save(updatedUser);

        return new UserResponse(updatedUser);
    }


    @Override
    public UserResponse deleteUser(int id) throws FirebaseAuthException, StripeException {
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

            if (!fcmTokenRepository.existsByDeviceIdAndUserId(fcmToken, userId)) {
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
    @Transactional
    public String removeFcmToken(int userId, String deviceId) {
        if (fcmTokenRepository.existsByDeviceIdAndUserId(deviceId, userId)) {
            fcmTokenRepository.deleteByDeviceIdAndUserId(deviceId, userId);
            return "FCM Token removed successfully";
        }
        return "FCM Token not found";
    }

    @Override
    public Object getFilteredUsers(Map<String, String> params, int page, int size, String fields, String sortBy, String direction) {
        PagingResponse<?> users = getUsers(params, page, size, sortBy, direction);
        return ResponseFilterUtil.getFilteredResponse(fields, users, "Return using dynamic filter successfully");
    }

    @Override
    public Optional<UserResponse> getUserById(int id) {
        return userRepository.findById(id)
                .map(user -> UserResponse
                        .builder()
                        .id(user.getId())
                        .firstname(user.getFirstname())
                        .lastname(user.getLastname())
                        .phone(user.getPhoneNumber())
                        .email(user.getEmail())
                        .address(user.getAddress())
                        .birthday(user.getDob())
                        .connectionId(user.getConnectionId())
                        .build());
    }


}
