package com.example.logistics_tracking.service;

import com.logistics.parcel.exception.BusinessException;
import com.example.logistics_tracking.dto.AgencyResponse;
import com.example.logistics_tracking.entity.Agency;
import com.example.logistics_tracking.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgencyService {

    private final AgencyRepository agencyRepository;

    @Transactional(readOnly = true)
    public Agency findById(String agencyId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(agencyId);
        } catch (IllegalArgumentException e) {
            throw BusinessException.agencyNotFound(agencyId);
        }

        return agencyRepository.findById(uuid)
                .orElseThrow(() -> BusinessException.agencyNotFound(agencyId));
    }

    @Transactional(readOnly = true)
    public List<AgencyResponse> getAllAgencies() {
        return agencyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgencyResponse> getAgenciesByCountry(String country) {
        return agencyRepository.findByCountry(country).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgencyResponse> getAgenciesByCountryAndTown(String country, String town) {
        return agencyRepository.findByCountryAndTown(country, town).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AgencyResponse mapToResponse(Agency agency) {
        return AgencyResponse.builder()
                .id(agency.getId())
                .name(agency.getName())
                .country(agency.getCountry())
                .town(agency.getTown())
                .addressLine(agency.getAddressLine())
                .latitude(agency.getLatitude())
                .longitude(agency.getLongitude())
                .build();
    }
}