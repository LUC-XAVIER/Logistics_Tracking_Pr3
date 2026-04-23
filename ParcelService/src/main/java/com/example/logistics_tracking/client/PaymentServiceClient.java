package com.example.logistics_tracking.client;

import com.example.logistics_tracking.dto.old.PaymentQuoteRequest;
import com.example.logistics_tracking.dto.old.PaymentQuoteResponse;
import com.example.logistics_tracking.dto.old.PaymentResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PaymentService", path = "/logistics/api/v1/payments")
public interface PaymentServiceClient {

    @PostMapping("/quote")
    PaymentQuoteResponse quote(@RequestBody PaymentQuoteRequest request);

    @GetMapping("/{paymentId}")
    PaymentResponse getPayment(@PathVariable("paymentId") UUID paymentId);
}
