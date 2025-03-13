package com.group2.glamping.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.group2.glamping.model.dto.requests.UserUpdateRequest;
import com.group2.glamping.model.dto.response.BaseResponse;
import com.group2.glamping.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Retrieve all users", description = "Get a list of all users in the system with pagination and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam Map<String, String> params,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "fields", required = false) String fields,
            @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
            @RequestParam(name = "direction", required = false, defaultValue = "ASC") String direction) {

        return ResponseEntity.ok(userService.getFilteredUsers(params, page, size, fields, sortBy, direction));

    }


    @Operation(summary = "Update user details", description = "Update the details of an existing user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to update", required = true)
            @PathVariable int id,
            @Valid @RequestBody
            @Parameter(description = "User update details", required = true)
            UserUpdateRequest request) {

        return new ResponseEntity<>(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated user")
                .data(userService.updateUser(id, request))
                .build(),
                HttpStatus.OK);
    }

    @Operation(summary = "Delete a user", description = "Delete a user from the system by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Integer id) throws FirebaseAuthException {
        return new ResponseEntity<>(BaseResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully deleted user")
                .data(userService.deleteUser(id))
                .build(), HttpStatus.OK);
    }
}
