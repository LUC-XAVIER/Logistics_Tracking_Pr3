package com.example.logistics_tracking.service;

import com.example.logistics_tracking.event.ParcelCreatedEvent;
import com.logistics.parcel.exception.BusinessException;
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
    private final GeocodingService geocodingService;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public ParcelResponse createParcel(ParcelRequest request) {
        log.info("Creating parcel for user: {}", request.getUserId());

        validateAddressInput(request);

        Parcel parcel = Parcel.builder()
                .userId(request.getUserId())
                .weight(request.getWeight())
                .fragility(request.getFragility())
                .status(ParcelStatus.PENDING_PAYMENT)
                .build();

        // Handle source location
        handleSourceLocation(parcel, request);

        // Handle destination location
        handleDestinationLocation(parcel, request);

        // Calculate cost and ETA
        calculateCostAndEta(parcel);

        // Save parcel
        Parcel savedParcel = parcelRepository.save(parcel);
        log.info("Parcel created: id={}", savedParcel.getId());

        // Publish event to Kafka
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

    private void validateAddressInput(ParcelRequest request) {
        // Source validation
        boolean hasSourceAgency = request.hasSourceAgency();
        boolean hasSourceManual = request.hasSourceManualAddress();

        if (hasSourceAgency && hasSourceManual) {
            throw BusinessException.invalidAddressInput();
        }
        if (!hasSourceAgency && !hasSourceManual) {
            throw BusinessException.missingAddressInput();
        }

        // Destination validation
        boolean hasDestAgency = request.hasDestAgency();
        boolean hasDestManual = request.hasDestManualAddress();

        if (hasDestAgency && hasDestManual) {
            throw BusinessException.invalidAddressInput();
        }
        if (!hasDestAgency && !hasDestManual) {
            throw BusinessException.missingAddressInput();
        }
    }

    private void handleSourceLocation(Parcel parcel, ParcelRequest request) {
        if (request.hasSourceAgency()) {
            Agency agency = agencyService.findById(request.getSourceAgencyId());
            parcel.setSourceAgency(agency);
            parcel.setSourceLatitude(agency.getLatitude());
            parcel.setSourceLongitude(agency.getLongitude());
            log.debug("Using source agency: {}", agency.getName());
        } else {
            parcel.setSourceManualAddress(request.getSourceManualAddress());
            Coordinates coords = geocodingService.getCoordinatesFromAddress(
                    request.getSourceManualAddress()
            );
            parcel.setSourceLatitude(coords.getLatitude());
            parcel.setSourceLongitude(coords.getLongitude());
            log.debug("Geocoded source address: {}", request.getSourceManualAddress());
        }
    }

    private void handleDestinationLocation(Parcel parcel, ParcelRequest request) {
        if (request.hasDestAgency()) {
            Agency agency = agencyService.findById(request.getDestAgencyId());
            parcel.setDestAgency(agency);
            parcel.setDestLatitude(agency.getLatitude());
            parcel.setDestLongitude(agency.getLongitude());
            log.debug("Using dest agency: {}", agency.getName());
        } else {
            parcel.setDestManualAddress(request.getDestManualAddress());
            Coordinates coords = geocodingService.getCoordinatesFromAddress(
                    request.getDestManualAddress()
            );
            parcel.setDestLatitude(coords.getLatitude());
            parcel.setDestLongitude(coords.getLongitude());
            log.debug("Geocoded dest address: {}", request.getDestManualAddress());
        }
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
                    .sourceManualAddress(parcel.getSourceManualAddress())
                    .sourceLatitude(parcel.getSourceLatitude())
                    .sourceLongitude(parcel.getSourceLongitude())
                    .destAgencyId(parcel.getDestAgency() != null ?
                            parcel.getDestAgency().getId().toString() : null)
                    .destManualAddress(parcel.getDestManualAddress())
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
                .sourceManualAddress(parcel.getSourceManualAddress())
                .sourceLatitude(parcel.getSourceLatitude())
                .sourceLongitude(parcel.getSourceLongitude())
                .destAgencyId(parcel.getDestAgency() != null ?
                        parcel.getDestAgency().getId().toString() : null)
                .destAgencyName(parcel.getDestAgency() != null ?
                        parcel.getDestAgency().getName() : null)
                .destManualAddress(parcel.getDestManualAddress())
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
}