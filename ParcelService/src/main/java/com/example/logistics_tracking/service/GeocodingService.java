package com.example.logistics_tracking.service;

import com.logistics.parcel.exception.BusinessException;
import com.example.logistics_tracking.entity.Coordinates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeocodingService {

    public Coordinates getCoordinatesFromAddress(String address) {

        log.info("Geocoding address: {}", address);

        // TODO: Implement actual geocoding API call
        // Options:
        // 1. Google Maps Geocoding API
        // 2. OpenStreetMap Nominatim
        // 3. Mapbox Geocoding API

        // For now, throw exception indicating implementation needed
        throw BusinessException.geocodingFailed(address);

        // Future implementation example:
        // RestTemplate restTemplate = new RestTemplate();
        // String url = "https://nominatim.openstreetmap.org/search?q=" + address + "&format=json";
        // ResponseEntity<JsonNode[]> response = restTemplate.getForEntity(url, JsonNode[].class);
        // Extract lat/lng from response
        // return new Coordinates(lat, lng);
    }
}