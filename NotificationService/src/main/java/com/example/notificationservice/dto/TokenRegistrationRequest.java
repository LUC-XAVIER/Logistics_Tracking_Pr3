package com.example.notificationservice.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Data;

@Data
public class TokenRegistrationRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private String token;
}