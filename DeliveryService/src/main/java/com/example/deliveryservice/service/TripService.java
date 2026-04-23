package com.example.deliveryservice.service;

import com.example.deliveryservice.client.ParcelServiceClient;
import com.example.deliveryservice.dto.*;
import com.example.deliveryservice.entity.DriverTrip;
import com.example.deliveryservice.entity.TripParcel;
import com.example.deliveryservice.entity.TripSegment;
import com.example.deliveryservice.enums.SegmentStatus;
import com.example.deliveryservice.enums.TripParcelStatus;
import com.example.deliveryservice.enums.TripStatus;
import com.example.deliveryservice.dto.TripRequest;
import com.example.deliveryservice.event.SegmentReachedEvent;
import com.example.deliveryservice.event.TripCompletedEvent;
import com.example.deliveryservice.exception.BusinessException;
import com.example.deliveryservice.repository.DriverTripRepository;
import com.example.deliveryservice.repository.TripParcelRepository;
import com.example.deliveryservice.repository.TripSegmentRepository;
import com.example.deliveryservice.service.OsrmService.OsrmRouteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripService {

    private final DriverTripRepository tripRepository;
    private final TripSegmentRepository segmentRepository;
    private final TripParcelRepository tripParcelRepository;
    private final OsrmService osrmService;
    private final KafkaEventPublisher kafkaEventPublisher;
    private final ParcelServiceClient parcelServiceClient;

    @Transactional
    public TripResponse createTrip(TripRequest request) {
        AgencyCoordinatesResponse sourceCoords =
                parcelServiceClient.getAgencyCoordinates(request.getSourceAgencyId());
        AgencyCoordinatesResponse destCoords =
                parcelServiceClient.getAgencyCoordinates(request.getDestAgencyId());

        OsrmRouteResult route = osrmService.calculateRoute(
                sourceCoords.getLatitude(), sourceCoords.getLongitude(),
                destCoords.getLatitude(), destCoords.getLongitude()
        );

        DriverTrip trip = DriverTrip.builder()
                .driverId(request.getDriverId())
                .sourceAgencyId(request.getSourceAgencyId())
                .destAgencyId(request.getDestAgencyId())
                .sourceLatitude(sourceCoords.getLatitude())
                .sourceLongitude(sourceCoords.getLongitude())
                .destLatitude(destCoords.getLatitude())
                .destLongitude(destCoords.getLongitude())
                .totalDistanceKm(route.totalDistanceKm())
                .segmentCount(route.segmentCount())
                .fullPath(route.fullPath())
                .status(TripStatus.COLLECTING)
                .build();

        DriverTrip savedTrip = tripRepository.save(trip);

        List<TripSegment> segments = buildSegments(savedTrip, route);
        segmentRepository.saveAll(segments);
        savedTrip.setSegments(segments);

        log.info("Trip created: id={}, distance={}km, segments={}",
                savedTrip.getId(), route.totalDistanceKm(), route.segmentCount());

        return mapToResponse(savedTrip);
    }

    @Transactional
    public void assignParcels(UUID tripId, List<String> parcelIds) {
        DriverTrip trip = findTripById(tripId);

        if (!trip.getStatus().canAcceptParcels()) {
            throw BusinessException.tripNotInCorrectStatus("COLLECTING");
        }

        for (String parcelId : parcelIds) {
            if (tripParcelRepository.existsByParcelIdAndStatus(parcelId, TripParcelStatus.ASSIGNED)) {
                throw BusinessException.parcelAlreadyAssigned(parcelId);
            }

            TripParcel tripParcel = TripParcel.builder()
                    .trip(trip)
                    .parcelId(parcelId)
                    .status(TripParcelStatus.ASSIGNED)
                    .build();

            tripParcelRepository.save(tripParcel);
        }

        log.info("Assigned {} parcels to trip {}", parcelIds.size(), tripId);
    }

    @Transactional
    public TripResponse startTrip(UUID tripId) {
        DriverTrip trip = findTripById(tripId);

        if (!trip.getStatus().canStart()) {
            throw BusinessException.tripNotInCorrectStatus("COLLECTING");
        }

        trip.setStatus(TripStatus.ACTIVE);
        trip.setStartedAt(Instant.now());
        DriverTrip savedTrip = tripRepository.save(trip);

        log.info("Trip started: id={}", tripId);

        return mapToResponse(savedTrip);
    }

    @Transactional
    public TripResponse markSegmentReached(UUID tripId, UUID segmentId) {
        DriverTrip trip = findTripById(tripId);

        if (!trip.getStatus().isActive()) {
            throw BusinessException.tripNotInCorrectStatus("ACTIVE");
        }

        TripSegment segment = segmentRepository.findById(segmentId)
                .orElseThrow(() -> BusinessException.segmentNotFound(segmentId));

        if (segment.getStatus() == SegmentStatus.REACHED) {
            throw BusinessException.segmentAlreadyReached(segmentId);
        }

        segment.setStatus(SegmentStatus.REACHED);
        segment.setReachedAt(Instant.now());
        segmentRepository.save(segment);

        List<String> parcelIds = tripParcelRepository.findByTripIdAndStatus(tripId, TripParcelStatus.ASSIGNED)
                .stream()
                .map(TripParcel::getParcelId)
                .collect(Collectors.toList());

        double distanceTraveled = segment.getDistanceFromStartKm();
        double distanceRemaining = trip.getTotalDistanceKm() - distanceTraveled;

        publishSegmentReachedEvent(trip, segment, parcelIds, distanceTraveled, distanceRemaining);

        boolean isLastSegment = segment.getSegmentOrder().equals(trip.getSegmentCount());
        if (isLastSegment) {
            completeTrip(trip, parcelIds);
        }

        log.info("Segment reached: tripId={}, segment={}/{}",
                tripId, segment.getSegmentOrder(), trip.getSegmentCount());

        return mapToResponse(tripRepository.findById(tripId).orElseThrow());
    }

    @Transactional(readOnly = true)
    public TripResponse getTrip(UUID tripId) {
        return mapToResponse(findTripById(tripId));
    }


  @Transactional(readOnly = true)
  public List<AvailableParcelResponse> getAvailableParcels(UUID tripId) {
    DriverTrip trip = findTripById(tripId);

    if (!trip.getStatus().canAcceptParcels()) {
      throw BusinessException.tripNotInCorrectStatus("COLLECTING");
    }

    try {
      return parcelServiceClient.getAvailableParcels(
        trip.getSourceAgencyId(),
        trip.getDestAgencyId()
      );
    } catch (Exception e) {
      log.error("Failed to fetch available parcels: {}", e.getMessage());
      return Collections.emptyList();
    }
  }

    private void completeTrip(DriverTrip trip, List<String> parcelIds) {
        trip.setStatus(TripStatus.COMPLETED);
        trip.setCompletedAt(Instant.now());

        tripParcelRepository.findByTripIdAndStatus(trip.getId(), TripParcelStatus.ASSIGNED)
                .forEach(tp -> {
                    tp.setStatus(TripParcelStatus.DELIVERED);
                    tp.setDeliveredAt(Instant.now());
                    tripParcelRepository.save(tp);
                });

        tripRepository.save(trip);

        TripCompletedEvent event = TripCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("TripCompleted")
                .timestamp(Instant.now())
                .tripId(trip.getId())
                .driverId(trip.getDriverId())
                .sourceAgencyId(trip.getSourceAgencyId())
                .destAgencyId(trip.getDestAgencyId())
                .totalDistanceKm(trip.getTotalDistanceKm())
                .parcelIds(parcelIds)
                .completedAt(trip.getCompletedAt())
                .build();

        kafkaEventPublisher.publishTripCompleted(event);

        log.info("Trip completed: id={}, parcels delivered={}", trip.getId(), parcelIds.size());
    }

    private void publishSegmentReachedEvent(DriverTrip trip, TripSegment segment,
                                            List<String> parcelIds,
                                            double distanceTraveled, double distanceRemaining) {
        SegmentReachedEvent event = SegmentReachedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SegmentReached")
                .timestamp(Instant.now())
                .tripId(trip.getId())
                .segmentId(segment.getId())
                .driverId(trip.getDriverId())
                .segmentOrder(segment.getSegmentOrder())
                .totalSegments(trip.getSegmentCount())
                .latitude(segment.getLatitude())
                .longitude(segment.getLongitude())
                .distanceTraveledKm(distanceTraveled)
                .distanceRemainingKm(distanceRemaining)
                .parcelIds(parcelIds)
                .build();

        kafkaEventPublisher.publishSegmentReached(event);
    }

    private List<TripSegment> buildSegments(DriverTrip trip, OsrmRouteResult route) {
        List<TripSegment> segments = new ArrayList<>();
        List<double[]> checkpoints = route.checkpoints();

        // Cumulative distances for each checkpoint
        double[] cumDistances = buildCumulativeDistances(checkpoints);

        for (int i = 0; i < checkpoints.size(); i++) {
            double[] point = checkpoints.get(i);
            TripSegment segment = TripSegment.builder()
                    .trip(trip)
                    .segmentOrder(i + 1)
                    .latitude(point[0])
                    .longitude(point[1])
                    .distanceFromStartKm(cumDistances[i] / 1000.0)
                    .status(SegmentStatus.PENDING)
                    .build();
            segments.add(segment);
        }

        return segments;
    }

    private double[] buildCumulativeDistances(List<double[]> checkpoints) {
        double[] cumDist = new double[checkpoints.size()];
        cumDist[0] = 0;
        for (int i = 1; i < checkpoints.size(); i++) {
            double dLat = Math.toRadians(checkpoints.get(i)[0] - checkpoints.get(i - 1)[0]);
            double dLng = Math.toRadians(checkpoints.get(i)[1] - checkpoints.get(i - 1)[1]);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(checkpoints.get(i - 1)[0]))
                    * Math.cos(Math.toRadians(checkpoints.get(i)[0]))
                    * Math.sin(dLng / 2) * Math.sin(dLng / 2);
            cumDist[i] = cumDist[i - 1] + 6371000 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        }
        return cumDist;
    }

    private DriverTrip findTripById(UUID tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> BusinessException.tripNotFound(tripId));
    }

    private TripResponse mapToResponse(DriverTrip trip) {
        List<SegmentResponse> segmentResponses = segmentRepository
                .findByTripIdOrderBySegmentOrderAsc(trip.getId())
                .stream()
                .map(s -> SegmentResponse.builder()
                        .id(s.getId())
                        .segmentOrder(s.getSegmentOrder())
                        .latitude(s.getLatitude())
                        .longitude(s.getLongitude())
                        .distanceFromStartKm(s.getDistanceFromStartKm())
                        .status(s.getStatus())
                        .reachedAt(s.getReachedAt())
                        .build())
                .collect(Collectors.toList());

        int parcelsCount = tripParcelRepository.findByTripId(trip.getId()).size();

        return TripResponse.builder()
                .id(trip.getId())
                .driverId(trip.getDriverId())
                .sourceAgencyId(trip.getSourceAgencyId())
                .destAgencyId(trip.getDestAgencyId())
                .totalDistanceKm(trip.getTotalDistanceKm())
                .segmentCount(trip.getSegmentCount())
                .status(trip.getStatus())
                .segments(segmentResponses)
                .parcelsCount(parcelsCount)
                .fullPath(trip.getFullPath())
                .startedAt(trip.getStartedAt())
                .createdAt(trip.getCreatedAt())
                .build();
    }
}
