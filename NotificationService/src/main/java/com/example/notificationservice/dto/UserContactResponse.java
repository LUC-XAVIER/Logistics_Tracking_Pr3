package com.example.notificationservice.dto;

import lombok.Data;

@Data
public class UserContactResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}