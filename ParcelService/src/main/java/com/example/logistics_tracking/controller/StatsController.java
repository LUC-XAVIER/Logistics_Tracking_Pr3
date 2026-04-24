package com.example.logistics_tracking.controller;

import com.example.logistics_tracking.dto.AdminStatsResponse;
import com.example.logistics_tracking.service.ParcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logistics/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ParcelService parcelService;

    @GetMapping("/admin/summary")
    public ResponseEntity<AdminStatsResponse> getAdminSummary() {
        return ResponseEntity.ok(parcelService.getAdminStats());
    }
}
