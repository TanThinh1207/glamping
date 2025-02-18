package com.group2.glamping.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.group2.glamping.model.dto.requests.UserUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully retrieved all users")
                .data(userService.getUsers())
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return new ResponseEntity<>(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully retrieved user by id")
                .data(userService.getUserById(id))
                .build(), HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    @Transactional
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @Valid @RequestBody UserUpdateRequest request) {

        return new ResponseEntity<>(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated user")
                .data(userService.updateUser(id, request))
                .build(),
                HttpStatus.OK);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) throws FirebaseAuthException {
        return new ResponseEntity<>(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully deleted user")
                .data(userService.deleteUser(id))
                .build(), HttpStatus.OK);
    }
}
