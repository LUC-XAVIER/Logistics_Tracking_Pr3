package com.example.usermanagementservice.controller;

import com.example.usermanagementservice.config.JwtUtil;
import com.example.usermanagementservice.dto.AuthRequest;
import com.example.usermanagementservice.dto.AuthResponse;
import com.example.usermanagementservice.dto.SignupRequest;
import com.example.usermanagementservice.dto.UserResponse;
import com.example.usermanagementservice.entity.User;
import com.example.usermanagementservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/logistics/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            User user = userService.getUserByEmail(userDetails.getUsername());

            if (user == null){
                logger.error("User not found after auth: {}", userDetails.getUsername());
            }

            String token = jwtUtil.generateToken(userDetails.getUsername());
            logger.info("user logged in successfully");

            return ResponseEntity.ok(new AuthResponse(token, mapToUserResponse(user)));
        } catch (BadCredentialsException ex){
            logger.warn("Invalid login attempt for user with email: {}", request.getEmail());
            return ResponseEntity.badRequest().body(Map.of(
                    "errorCode", "INVALID_CREDENTIALS",
                    "message", "Invalid credentials: wrong email or password"
            ));

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An error occurred during login"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        boolean alreadyExists = userService.existsByEmail(request.getEmail()) || userService.existsByPhoneNumber(request.getPhoneNumber());
        if (alreadyExists) {
            logger.warn("Attempt to register with existing email and phone number: {} : {}, request.getEmail(), request.phoneNumber", request.getEmail(), request.getPhoneNumber());
            return ResponseEntity.badRequest().body(Map.of(
                    "errorCode", "EMAIL_OR_NUMBER_ALREADY_EXISTS",
                    "message", "A user with this email or number already exists"
            ));
        }

        try{
            User user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .address(request.getAddress())
                    .email(request.getEmail())
                    .phoneNumber(request.getPhoneNumber())
                    .role(request.getRole())
                    .password(NoOpPasswordEncoder.getInstance().encode(request.getPassword()))
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .isDeleted(false)
                    .build();

            userService.createUser(user);

            return ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(user.getEmail()), mapToUserResponse(user)));
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "errorCode", "INTERNAL_SERVER_ERROR",
                    "message", "An error occurred during registration"
            ));
        }
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

