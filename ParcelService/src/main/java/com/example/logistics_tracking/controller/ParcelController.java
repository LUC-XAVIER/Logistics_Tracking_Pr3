package com.example.logistics_tracking.controller;

import com.example.logistics_tracking.dto.ParcelRequest;
import com.example.logistics_tracking.dto.ParcelResponse;
import com.example.logistics_tracking.enums.ParcelStatus;
import com.example.logistics_tracking.service.ParcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parcels")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(@Valid @RequestBody ParcelRequest request) {
        ParcelResponse response = parcelService.createParcel(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{parcelId}")
    public ResponseEntity<ParcelResponse> getParcel(@PathVariable String parcelId) {
        ParcelResponse response = parcelService.getParcelById(parcelId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ParcelResponse>> getUserParcels(@PathVariable String userId) {
        List<ParcelResponse> parcels = parcelService.getParcelsByUserId(userId);
        return ResponseEntity.ok(parcels);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ParcelResponse>> getParcelsByStatus(@PathVariable ParcelStatus status) {
        List<ParcelResponse> parcels = parcelService.getParcelsByStatus(status);
        return ResponseEntity.ok(parcels);
    }
}