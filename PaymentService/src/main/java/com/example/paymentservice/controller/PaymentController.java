package com.example.paymentservice.controller;

import com.example.paymentservice.dto.CreatePaymentRequest;
import com.example.paymentservice.dto.PaymentQuoteRequest;
import com.example.paymentservice.dto.PaymentQuoteResponse;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.dto.UpdatePaymentStatusRequest;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentStatus;
import com.example.paymentservice.repository.PaymentRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private static final BigDecimal BASE_PRICE = BigDecimal.valueOf(5000);
    private static final BigDecimal FRAGILITY_UNIT_FEE = BigDecimal.valueOf(500);
    private static final BigDecimal DISTANCE_UNIT_FEE = BigDecimal.valueOf(100);

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/quote")
    public PaymentQuoteResponse quote(@Valid @RequestBody PaymentQuoteRequest request) {
        BigDecimal baseAmount = BASE_PRICE;
        BigDecimal fragilityFee = FRAGILITY_UNIT_FEE.multiply(BigDecimal.valueOf(request.fragilityLevel()));
        BigDecimal safeDistance = BigDecimal.valueOf(request.distanceKm() != null ? request.distanceKm() : 0.0d);
        BigDecimal distanceFee = safeDistance.multiply(DISTANCE_UNIT_FEE);
        BigDecimal totalAmount = baseAmount.add(fragilityFee).add(distanceFee);

        return new PaymentQuoteResponse(
                request.parcelId(),
                baseAmount,
                fragilityFee,
                distanceFee,
                totalAmount,
                "XAF"
        );
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = paymentRepository.save(Payment.builder()
                .parcelId(request.parcelId())
                .paymentMethod(request.paymentMethod())
                .amount(request.quotedAmount() != null ? request.quotedAmount() : BASE_PRICE)
                .currency(request.currency() != null ? request.currency() : "XAF")
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(payment));
    }

    @GetMapping
    public List<PaymentResponse> list() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> get(@PathVariable UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable UUID paymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request
    ) {
        return paymentRepository.findById(paymentId)
                .map(payment -> {
                    payment.setStatus(request.status());
                    return ResponseEntity.ok(toResponse(paymentRepository.save(payment)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentStatus status = payment.getStatus();
        return new PaymentResponse(
                payment.getId(),
                payment.getParcelId(),
                status,
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
