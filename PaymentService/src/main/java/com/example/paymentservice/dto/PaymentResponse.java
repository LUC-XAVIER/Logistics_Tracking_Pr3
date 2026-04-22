package com.example.paymentservice.dto;

import com.example.paymentservice.enums.PaymentMethod;
import com.example.paymentservice.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private UUID id;
    private String parcelId;
    private UUID userId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private String failureReason;
    private Instant paidAt;
    private Instant createdAt;
}