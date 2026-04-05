package com.example.logistics_tracking.controller;

import com.example.logistics_tracking.dto.CreateParcelRequest;
import com.example.logistics_tracking.dto.ParcelResponse;
import com.example.logistics_tracking.dto.ParcelSummaryResponse;
import com.example.logistics_tracking.dto.UpdateParcelStatusRequest;
import com.example.logistics_tracking.entity.Parcel;
import com.example.logistics_tracking.repository.ParcelRepository;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/parcels")
public class ParcelController {

    private final ParcelRepository parcelRepository;

    public ParcelController(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    @PostMapping
    public ResponseEntity<ParcelResponse> create(@Valid @RequestBody CreateParcelRequest request) {
        Parcel parcel = parcelRepository.save(Parcel.builder()
                .sourceAgencyId(request.sourceAgencyId())
                .destinationAgencyId(request.destinationAgencyId())
                .sourceAddress(request.sourceAddress())
                .destinationAddress(request.destinationAddress())
                .sourceLatitude(request.sourceLatitude())
                .sourceLongitude(request.sourceLongitude())
                .destinationLatitude(request.destinationLatitude())
                .destinationLongitude(request.destinationLongitude())
                .weightKg(request.weightKg())
                .fragilityLevel(request.fragilityLevel())
                .estimatedDeliveryTime(request.estimatedDeliveryTime())
                .routeSegments(request.routeSegments())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(parcel));
    }

    @GetMapping
    public List<ParcelResponse> list() {
        return parcelRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{parcelId}")
    public ResponseEntity<ParcelResponse> get(@PathVariable UUID parcelId) {
        return parcelRepository.findById(parcelId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{parcelId}/summary")
    public ResponseEntity<ParcelSummaryResponse> getSummary(@PathVariable UUID parcelId) {
        return parcelRepository.findById(parcelId)
                .map(this::toSummary)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{parcelId}/status")
    public ResponseEntity<ParcelResponse> updateStatus(
            @PathVariable UUID parcelId,
            @Valid @RequestBody UpdateParcelStatusRequest request
    ) {
        return parcelRepository.findById(parcelId)
                .map(parcel -> {
                    parcel.setStatus(request.status());
                    if (request.paymentId() != null) {
                        parcel.setPaymentId(request.paymentId());
                    }
                    if (request.deliveryId() != null) {
                        parcel.setDeliveryId(request.deliveryId());
                    }
                    return ResponseEntity.ok(toResponse(parcelRepository.save(parcel)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ParcelResponse toResponse(Parcel parcel) {
        return new ParcelResponse(
                parcel.getId(),
                parcel.getSourceAgencyId(),
                parcel.getDestinationAgencyId(),
                parcel.getSourceAddress(),
                parcel.getDestinationAddress(),
                parcel.getSourceLatitude(),
                parcel.getSourceLongitude(),
                parcel.getDestinationLatitude(),
                parcel.getDestinationLongitude(),
                parcel.getWeightKg(),
                parcel.getFragilityLevel(),
                parcel.getStatus(),
                parcel.getEstimatedDeliveryTime(),
                parcel.getRouteSegments(),
                parcel.getPaymentId(),
                parcel.getDeliveryId(),
                parcel.getCreatedAt(),
                parcel.getUpdatedAt()
        );
    }

    private ParcelSummaryResponse toSummary(Parcel parcel) {
        return new ParcelSummaryResponse(
                parcel.getId(),
                parcel.getWeightKg(),
                parcel.getFragilityLevel(),
                parcel.getStatus(),
                parcel.getSourceLatitude(),
                parcel.getSourceLongitude(),
                parcel.getDestinationLatitude(),
                parcel.getDestinationLongitude(),
                parcel.getEstimatedDeliveryTime()
        );
    }
}
