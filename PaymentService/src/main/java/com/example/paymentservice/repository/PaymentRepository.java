package com.example.paymentservice.repository;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByParcelId(String parcelId);

    List<Payment> findByUserId(UUID userId);

    List<Payment> findByUserIdAndStatus(UUID userId, PaymentStatus status);

    boolean existsByParcelIdAndStatus(String parcelId, PaymentStatus status);
}