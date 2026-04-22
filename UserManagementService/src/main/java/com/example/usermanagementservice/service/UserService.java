package com.example.usermanagementservice.service;

import com.example.usermanagementservice.dto.*;
import com.example.usermanagementservice.entity.User;
import com.example.usermanagementservice.enums.Role;
import com.example.usermanagementservice.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmailAndIsDeletedFalse(email);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepo.existsByPhoneNumber(phoneNumber);
    }

    public UserResponse getUserById(UUID userId) {
        User user = findActiveUserById(userId);
        return mapToUserResponse(user);
    }

    public UserContactResponse getUserContactById(UUID userId) {
        User user = findActiveUserById(userId);
        return UserContactResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = findActiveUserById(userId);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getAddress() != null) user.setAddress(request.getAddress());

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepo.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number already in use");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        user.setUpdatedAt(new Date());
        return mapToUserResponse(userRepo.save(user));
    }

    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = findActiveUserById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(new Date());
        userRepo.save(user);
        logger.info("Password changed for userId={}", userId);
    }

    public void softDeleteUser(UUID userId) {
        User user = findActiveUserById(userId);
        user.setDeleted(true);
        user.setUpdatedAt(new Date());
        userRepo.save(user);
        logger.info("User soft deleted: userId={}", userId);
    }

    public List<UserResponse> getAllUsers() {
        return userRepo.findAllByIsDeletedFalse()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepo.findByRoleAndIsDeletedFalse(role)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private User findActiveUserById(UUID userId) {
        return userRepo.findById(userId)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> {
                    logger.warn("User not found or deleted: userId={}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}