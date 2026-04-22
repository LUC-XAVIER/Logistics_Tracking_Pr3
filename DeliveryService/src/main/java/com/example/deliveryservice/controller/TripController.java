package com.example.deliveryservice.controller;

import com.example.deliveryservice.dto.AvailableParcelResponse;
import com.example.deliveryservice.dto.TripRequest;
import com.example.deliveryservice.dto.TripResponse;
import com.example.deliveryservice.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest request) {
        TripResponse response = tripService.createTrip(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable UUID tripId) {
        TripResponse response = tripService.getTrip(tripId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tripId}/available-parcels")
    public ResponseEntity<List<AvailableParcelResponse>> getAvailableParcels(
            @PathVariable UUID tripId) {
        List<AvailableParcelResponse> parcels = tripService.getAvailableParcels(tripId);
        return ResponseEntity.ok(parcels);
    }

    @PostMapping("/{tripId}/parcels")
    public ResponseEntity<Void> assignParcels(
            @PathVariable UUID tripId,
            @RequestBody List<String> parcelIds) {
        tripService.assignParcels(tripId, parcelIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripId}/start")
    public ResponseEntity<TripResponse> startTrip(@PathVariable UUID tripId) {
        TripResponse response = tripService.startTrip(tripId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{tripId}/segments/{segmentId}/reach")
    public ResponseEntity<TripResponse> markSegmentReached(
            @PathVariable UUID tripId,
            @PathVariable UUID segmentId) {
        TripResponse response = tripService.markSegmentReached(tripId, segmentId);
        return ResponseEntity.ok(response);
    }
}
