package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.dto.*;
import com.example.usermanagementservice.enums.Role;
import com.example.usermanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/logistics/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/{userId}/contact")
    public ResponseEntity<?> getUserContact(@PathVariable UUID userId) {
        try {
            UserContactResponse contact = userService.getUserContactById(userId);
            return ResponseEntity.ok(contact);
        } catch (RuntimeException e) {
            logger.warn("Contact lookup failed for userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "errorCode", "USER_NOT_FOUND",
                    "message", "User with ID " + userId + " not found"
            ));
        } catch (Exception e) {
            logger.error("Unexpected error for userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred"
            ));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "errorCode", "USER_NOT_FOUND",
                    "message", "User with ID " + userId + " not found"
            ));
        } catch (Exception e) {
            logger.error("Unexpected error for userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred"
            ));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId,
                                        @RequestBody UpdateUserRequest request) {
        try {
            UserResponse updated = userService.updateUser(userId, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "errorCode", "UPDATE_FAILED",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error updating userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred"
            ));
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable UUID userId,
                                            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(userId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Password changed successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "errorCode", "PASSWORD_CHANGE_FAILED",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error changing password for userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred"
            ));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        try {
            userService.softDeleteUser(userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Account deleted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "errorCode", "USER_NOT_FOUND",
                    "message", "User with ID " + userId + " not found"
            ));
        } catch (Exception e) {
            logger.error("Unexpected error deleting userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred"
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Unexpected error fetching all users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred"
            ));
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable Role role) {
        try {
            List<UserResponse> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Unexpected error fetching users by role={}: {}", role, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An unexpected error occurred"
            ));
        }
    }
}