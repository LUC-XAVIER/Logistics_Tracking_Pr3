package com.example.usermanagementservice;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        String password = "123456"; // Replace with your input
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(password);
        System.out.println("BCrypt hash: " + hashed);
    }

}
