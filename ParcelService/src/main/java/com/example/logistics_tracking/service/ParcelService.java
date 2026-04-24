package com.example.logistics_tracking.service;

import com.example.logistics_tracking.event.ParcelCreatedEvent;
import com.example.logistics_tracking.exception.BusinessException;
import com.example.logistics_tracking.entity.Coordinates;
import com.example.logistics_tracking.dto.AdminStatsResponse;
import com.example.logistics_tracking.dto.ParcelQuoteRequest;
import com.example.logistics_tracking.dto.ParcelQuoteResponse;
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
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
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

    @Transactional(readOnly = true)
    public ParcelQuoteResponse getQuote(ParcelQuoteRequest request) {
        Agency sourceAgency = agencyService.findById(request.getSourceAgencyId().toString());
        Agency destAgency = agencyService.findById(request.getDestAgencyId().toString());

        double distance = calculateDistance(
                sourceAgency.getLatitude(), sourceAgency.getLongitude(),
                destAgency.getLatitude(), destAgency.getLongitude()
        );

        BigDecimal cost = calculateCost(distance, request.getWeight(), request.getFragility());
        LocalDateTime eta = calculateEta(distance, request.getFragility());

        return ParcelQuoteResponse.builder()
                .estimatedCost(cost)
                .estimatedDeliveryTime(eta)
                .distanceKm(distance)
                .sourceAgencyName(sourceAgency.getName())
                .destAgencyName(destAgency.getName())
                .build();
    }

    private void calculateCostAndEta(Parcel parcel) {
        double distance = calculateDistance(
                parcel.getSourceLatitude(), parcel.getSourceLongitude(),
                parcel.getDestLatitude(), parcel.getDestLongitude()
        );

        parcel.setEstimatedCost(calculateCost(distance, parcel.getWeight(), parcel.getFragility()));
        parcel.setEstimatedDeliveryTime(calculateEta(distance, parcel.getFragility()));

        log.debug("Calculated cost: {} XAF, distance: {} km, ETA: {}",
                parcel.getEstimatedCost(), distance, parcel.getEstimatedDeliveryTime());
    }

    private BigDecimal calculateCost(double distance, double weight, int fragility) {
        BigDecimal baseCost = BigDecimal.valueOf(5000);
        BigDecimal distanceCost = BigDecimal.valueOf(distance * 100);
        BigDecimal fragilityCost = BigDecimal.valueOf(fragility * 500);
        BigDecimal weightCost = BigDecimal.valueOf(weight * 200);

        return baseCost
                .add(distanceCost)
                .add(fragilityCost)
                .add(weightCost);
    }

    private LocalDateTime calculateEta(double distance, int fragility) {
        double baseSpeed = 60.0;
        double adjustedSpeed = baseSpeed * (1 - (fragility / 15.0));
        double travelHours = distance / adjustedSpeed;

        return LocalDateTime.now().plusHours((long) Math.ceil(travelHours));
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
                .receiverName(parcel.getReceiverName())
                .receiverPhone(parcel.getReceiverPhone())
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
                ParcelStatus.WAITING_FOR_AGENT
        );

        return parcels.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateParcelStatusToWaitingForAgent(String parcelId) {
        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> BusinessException.parcelNotFound(parcelId));

        if (parcel.getStatus() == ParcelStatus.PENDING_PAYMENT) {
            parcel.setStatus(ParcelStatus.WAITING_FOR_AGENT);
            parcelRepository.save(parcel);
            log.info("Parcel {} status updated to WAITING_FOR_AGENT", parcelId);
        } else {
            log.warn("Parcel {} is not in PENDING_PAYMENT status. Current status: {}", parcelId, parcel.getStatus());
        }
    }

    @Transactional
    public void updateParcelsToInTransit(List<String> parcelIds) {
        log.info("Updating {} parcels to IN_TRANSIT", parcelIds.size());
        for (String id : parcelIds) {
            parcelRepository.findById(id).ifPresent(p -> {
                if (p.getStatus() == ParcelStatus.WAITING_FOR_AGENT) {
                    p.setStatus(ParcelStatus.IN_TRANSIT);
                    parcelRepository.save(p);
                    log.debug("Parcel {} status updated to IN_TRANSIT", id);
                }
            });
        }
    }

    @Transactional
    public void updateParcelsToDelivered(List<String> parcelIds) {
        log.info("Updating {} parcels to DELIVERED", parcelIds.size());
        for (String id : parcelIds) {
            parcelRepository.findById(id).ifPresent(p -> {
                if (p.getStatus() == ParcelStatus.IN_TRANSIT) {
                    p.setStatus(ParcelStatus.DELIVERED);
                    parcelRepository.save(p);
                    log.debug("Parcel {} status updated to DELIVERED", id);
                }
            });
        }
    }

    @Transactional
    public void cancelParcel(String parcelId) {
        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> BusinessException.parcelNotFound(parcelId));
        if (!parcel.getStatus().canBeCancelled()) {
            throw BusinessException.invalidStatus("Cannot cancel parcel in " + parcel.getStatus() + " state");
        }
        parcel.setStatus(ParcelStatus.CANCELLED);
        parcelRepository.save(parcel);
        log.info("Parcel {} has been cancelled", parcelId);
    }

    @Transactional(readOnly = true)
    public List<ParcelResponse> getAllParcels() {
        return parcelRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getAdminStats() {
        List<Parcel> allParcels = parcelRepository.findAll();
        long totalAgencies = agencyService.getAllAgencies().size();

        BigDecimal totalRevenue = allParcels.stream()
                .filter(p -> p.getStatus() == ParcelStatus.DELIVERED)
                .map(Parcel::getEstimatedCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return AdminStatsResponse.builder()
                .totalParcels(allParcels.size())
                .activeParcels(allParcels.stream().filter(p -> p.getStatus() == ParcelStatus.IN_TRANSIT).count())
                .deliveredParcels(allParcels.stream().filter(p -> p.getStatus() == ParcelStatus.DELIVERED).count())
                .totalRevenue(totalRevenue)
                .totalAgencies(totalAgencies)
                .build();
    }
}
