package com.example.deliveryservice.controller;

import com.example.deliveryservice.dto.CreateDeliveryRequest;
import com.example.deliveryservice.dto.DeliveryCheckpointResponse;
import com.example.deliveryservice.dto.DeliveryResponse;
import com.example.deliveryservice.dto.DeliverySummaryResponse;
import com.example.deliveryservice.dto.UpdateDeliveryStatusRequest;
import com.example.deliveryservice.dto.UpdateLocationRequest;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.entity.DeliveryCheckpoint;
import com.example.deliveryservice.repository.DeliveryCheckpointRepository;
import com.example.deliveryservice.repository.DeliveryRepository;
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
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryCheckpointRepository deliveryCheckpointRepository;

    public DeliveryController(DeliveryRepository deliveryRepository,
                              DeliveryCheckpointRepository deliveryCheckpointRepository) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryCheckpointRepository = deliveryCheckpointRepository;
    }

    @PostMapping
    public ResponseEntity<DeliverySummaryResponse> create(@RequestBody CreateDeliveryRequest request) {
        Delivery delivery = deliveryRepository.save(Delivery.builder()
                .parcelId(request.parcelId())
                .assignedAgentId(request.assignedAgentId())
                .status(request.status())
                .estimatedArrival(request.estimatedArrival())
                .routeSegments(request.routeSegments())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toSummary(delivery));
    }

    @GetMapping
    public List<DeliveryResponse> list() {
        return deliveryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponse> get(@PathVariable UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<DeliverySummaryResponse> getByParcel(@PathVariable UUID parcelId) {
        return deliveryRepository.findByParcelId(parcelId)
                .map(this::toSummary)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request
    ) {
        return deliveryRepository.findById(deliveryId)
                .map(delivery -> {
                    delivery.setStatus(request.status());
                    return ResponseEntity.ok(toResponse(deliveryRepository.save(delivery)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{deliveryId}/location")
    public ResponseEntity<DeliveryResponse> updateLocation(
            @PathVariable UUID deliveryId,
            @Valid @RequestBody UpdateLocationRequest request
    ) {
        return deliveryRepository.findById(deliveryId)
                .map(delivery -> {
                    delivery.setCurrentLatitude(request.latitude());
                    delivery.setCurrentLongitude(request.longitude());
                    delivery.setProgressPercentage((double) request.progressPercentage());
                    Delivery saved = deliveryRepository.save(delivery);

                    DeliveryCheckpoint checkpoint = DeliveryCheckpoint.builder()
                            .deliveryId(saved.getId())
                            .latitude(request.latitude())
                            .longitude(request.longitude())
                            .roadType(request.roadType())
                            .sequenceNumber(request.sequenceNumber())
                            .build();
                    deliveryCheckpointRepository.save(checkpoint);

                    return ResponseEntity.ok(toResponse(saved));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        List<DeliveryCheckpointResponse> checkpoints = deliveryCheckpointRepository
                .findByDeliveryIdOrderBySequenceNumberAsc(delivery.getId())
                .stream()
                .map(this::toCheckpointResponse)
                .toList();
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getParcelId(),
                delivery.getAssignedAgentId(),
                delivery.getStatus(),
                delivery.getCurrentLatitude(),
                delivery.getCurrentLongitude(),
                delivery.getProgressPercentage(),
                delivery.getEstimatedArrival(),
                delivery.getRouteSegments(),
                checkpoints,
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }

    private DeliverySummaryResponse toSummary(Delivery delivery) {
        return new DeliverySummaryResponse(
                delivery.getId(),
                delivery.getParcelId(),
                delivery.getStatus() != null ? delivery.getStatus().name() : null,
                delivery.getCurrentLatitude(),
                delivery.getCurrentLongitude(),
                delivery.getEstimatedArrival()
        );
    }

    private DeliveryCheckpointResponse toCheckpointResponse(DeliveryCheckpoint checkpoint) {
        return new DeliveryCheckpointResponse(
                checkpoint.getId(),
                checkpoint.getDeliveryId(),
                checkpoint.getLatitude(),
                checkpoint.getLongitude(),
                checkpoint.getRoadType(),
                checkpoint.getSequenceNumber(),
                checkpoint.getRecordedAt()
        );
    }
}
