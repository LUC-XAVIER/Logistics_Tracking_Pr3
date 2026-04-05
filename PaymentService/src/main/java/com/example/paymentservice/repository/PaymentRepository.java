package com.example.paymentservice.repository;

import com.example.paymentservice.entity.Payment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByParcelId(UUID parcelId);
}
