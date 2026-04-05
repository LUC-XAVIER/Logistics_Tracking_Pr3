package com.example.logistics_tracking.controller;

import com.example.logistics_tracking.dto.AgencyRequest;
import com.example.logistics_tracking.dto.AgencyResponse;
import com.example.logistics_tracking.entity.Agency;
import com.example.logistics_tracking.repository.AgencyRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/agencies")
public class AgencyController {

    private final AgencyRepository agencyRepository;

    public AgencyController(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    @PostMapping
    public ResponseEntity<AgencyResponse> create(@Valid @RequestBody AgencyRequest request) {
        Agency agency = agencyRepository.save(Agency.builder()
                .name(request.name())
                .country(request.country())
                .town(request.town())
                .addressLine(request.addressLine())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(agency));
    }

    @GetMapping
    public List<AgencyResponse> list() {
        return agencyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{agencyId}")
    public ResponseEntity<AgencyResponse> get(@PathVariable UUID agencyId) {
        return agencyRepository.findById(agencyId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private AgencyResponse toResponse(Agency agency) {
        return new AgencyResponse(
                agency.getId(),
                agency.getName(),
                agency.getCountry(),
                agency.getTown(),
                agency.getAddressLine(),
                agency.getLatitude(),
                agency.getLongitude(),
                agency.getCreatedAt(),
                agency.getUpdatedAt()
        );
    }
}
