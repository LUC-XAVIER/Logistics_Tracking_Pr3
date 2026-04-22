package com.example.paymentservice.controller;

import com.example.paymentservice.dto.PayRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;

  @GetMapping("/{paymentId}")
  public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID paymentId) {
    return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
  }

  @GetMapping("/parcel/{parcelId}")
  public ResponseEntity<PaymentResponse> getPaymentByParcelId(@PathVariable String parcelId) {
    return ResponseEntity.ok(paymentService.getPaymentByParcelId(parcelId));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentResponse>> getPaymentsByUserId(@PathVariable UUID userId) {
    return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
  }

  @PostMapping("/{paymentId}/pay")
  public ResponseEntity<PaymentResponse> pay(
    @PathVariable UUID paymentId,
    @Valid @RequestBody PayRequest request) {
    return ResponseEntity.ok(paymentService.pay(paymentId, request));
  }
}
