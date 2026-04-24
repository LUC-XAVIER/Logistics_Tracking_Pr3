package com.example.usermanagementservice.repository;

import com.example.usermanagementservice.entity.User;
import com.example.usermanagementservice.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    User findByEmailAndIsDeletedFalse(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    List<User> findByRoleAndIsDeletedFalse(Role role);
    List<User> findAllByIsDeletedFalse();
}
