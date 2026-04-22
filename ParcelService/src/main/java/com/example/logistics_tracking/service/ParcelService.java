package com.example.logistics_tracking.service;

import com.example.logistics_tracking.event.ParcelCreatedEvent;
import com.example.logistics_tracking.exception.BusinessException;
import com.example.logistics_tracking.entity.Coordinates;
import com.example.logistics_tracking.dto.ParcelRequest;
import com.example.logistics_tracking.dto.ParcelResponse;
import com.example.logistics_tracking.entity.Agency;
import com.example.logistics_tracking.entity.Parcel;
import com.example.logistics_tracking.enums.ParcelStatus;
import com.example.logistics_tracking.repository.ParcelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final AgencyService agencyService;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public ParcelResponse createParcel(ParcelRequest request) {
        Agency sourceAgency = agencyService.findById(request.getSourceAgencyId());
        Agency destAgency = agencyService.findById(request.getDestAgencyId());

        Parcel parcel = Parcel.builder()
                .userId(request.getUserId())
                .sourceAgency(sourceAgency)
                .sourceLatitude(sourceAgency.getLatitude())
                .sourceLongitude(sourceAgency.getLongitude())
                .destAgency(destAgency)
                .destLatitude(destAgency.getLatitude())
                .destLongitude(destAgency.getLongitude())
                .weight(request.getWeight())
                .fragility(request.getFragility())
                .status(ParcelStatus.PENDING_PAYMENT)
                .build();

        calculateCostAndEta(parcel);

        Parcel savedParcel = parcelRepository.save(parcel);
        publishParcelCreatedEvent(savedParcel);

        return mapToResponse(savedParcel);
    }

    @Transactional(readOnly = true)
    public ParcelResponse getParcelById(String parcelId) {
        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> BusinessException.parcelNotFound(parcelId));
        return mapToResponse(parcel);
    }

    @Transactional(readOnly = true)
    public List<ParcelResponse> getParcelsByUserId(String userId) {
        return parcelRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParcelResponse> getParcelsByStatus(ParcelStatus status) {
        return parcelRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void calculateCostAndEta(Parcel parcel) {
        double distance = calculateDistance(
                parcel.getSourceLatitude(), parcel.getSourceLongitude(),
                parcel.getDestLatitude(), parcel.getDestLongitude()
        );

        BigDecimal baseCost = BigDecimal.valueOf(5000);
        BigDecimal distanceCost = BigDecimal.valueOf(distance * 100);
        BigDecimal fragilityCost = BigDecimal.valueOf(parcel.getFragility() * 500);
        BigDecimal weightCost = BigDecimal.valueOf(parcel.getWeight() * 200);

        BigDecimal totalCost = baseCost
                .add(distanceCost)
                .add(fragilityCost)
                .add(weightCost);

        parcel.setEstimatedCost(totalCost);

        double baseSpeed = 60.0;
        double adjustedSpeed = baseSpeed * (1 - (parcel.getFragility() / 15.0));
        double travelHours = distance / adjustedSpeed;

        parcel.setEstimatedDeliveryTime(
                LocalDateTime.now().plusHours((long) Math.ceil(travelHours))
        );

        log.debug("Calculated cost: {} XAF, distance: {} km, ETA: {}",
                totalCost, distance, parcel.getEstimatedDeliveryTime());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private void publishParcelCreatedEvent(Parcel parcel) {
        try {
            ParcelCreatedEvent event = ParcelCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("ParcelCreated")
                    .timestamp(LocalDateTime.now())
                    .parcelId(parcel.getId())
                    .userId(parcel.getUserId())
                    .sourceAgencyId(parcel.getSourceAgency() != null ?
                            parcel.getSourceAgency().getId().toString() : null)
                    .sourceLatitude(parcel.getSourceLatitude())
                    .sourceLongitude(parcel.getSourceLongitude())
                    .destAgencyId(parcel.getDestAgency() != null ?
                            parcel.getDestAgency().getId().toString() : null)
                    .destLatitude(parcel.getDestLatitude())
                    .destLongitude(parcel.getDestLongitude())
                    .weight(parcel.getWeight())
                    .fragility(parcel.getFragility())
                    .estimatedCost(parcel.getEstimatedCost())
                    .estimatedDeliveryTime(parcel.getEstimatedDeliveryTime())
                    .build();

            kafkaEventPublisher.publishParcelCreated(event);

        } catch (Exception e) {
            log.error("Failed to publish ParcelCreated event for parcel: {}", parcel.getId(), e);
        }
    }

    private ParcelResponse mapToResponse(Parcel parcel) {
        return ParcelResponse.builder()
                .id(parcel.getId())
                .userId(parcel.getUserId())
                .sourceAgencyId(parcel.getSourceAgency() != null ?
                        parcel.getSourceAgency().getId().toString() : null)
                .sourceAgencyName(parcel.getSourceAgency() != null ?
                        parcel.getSourceAgency().getName() : null)
                .sourceLatitude(parcel.getSourceLatitude())
                .sourceLongitude(parcel.getSourceLongitude())
                .destAgencyId(parcel.getDestAgency() != null ?
                        parcel.getDestAgency().getId().toString() : null)
                .destAgencyName(parcel.getDestAgency() != null ?
                        parcel.getDestAgency().getName() : null)
                .destLatitude(parcel.getDestLatitude())
                .destLongitude(parcel.getDestLongitude())
                .weight(parcel.getWeight())
                .fragility(parcel.getFragility())
                .status(parcel.getStatus())
                .estimatedCost(parcel.getEstimatedCost())
                .estimatedDeliveryTime(parcel.getEstimatedDeliveryTime())
                .createdAt(parcel.getCreatedAt())
                .build();
    }
    @Transactional(readOnly = true)
    public UUID getParcelOwner(String parcelId) {
        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> BusinessException.parcelNotFound(parcelId));
        return parcel.getUserId();
    }
  public List<ParcelResponse> getAvailableParcels(UUID sourceAgencyId, UUID destAgencyId) {
    List<Parcel> parcels = parcelRepository.findBySourceAgencyIdAndDestAgencyIdAndStatus(
      sourceAgencyId,
      destAgencyId,
      ParcelStatus.WAITING_FOR_DRIVER
    );

    return parcels.stream()
      .map(this::mapToResponse)
      .collect(Collectors.toList());
  }
}
