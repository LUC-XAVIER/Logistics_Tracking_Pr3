package com.example.usermanagementservice.service;

import com.example.usermanagementservice.entity.User;
import com.example.usermanagementservice.repository.UserRepo;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmailAndIsDeletedFalse(email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of()
        );
    }
}
