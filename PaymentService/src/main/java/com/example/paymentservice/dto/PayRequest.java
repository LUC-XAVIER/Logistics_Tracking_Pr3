package com.example.paymentservice.dto;

import com.example.paymentservice.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}