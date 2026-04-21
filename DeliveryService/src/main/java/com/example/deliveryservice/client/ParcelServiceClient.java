package com.example.deliveryservice.client;

import com.example.deliveryservice.dto.AgencyCoordinatesResponse;
import com.example.deliveryservice.exception.BusinessException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
public class ParcelServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Getter
    @Value("${services.parcel-service.url}")
    private String parcelServiceUrl;

    public AgencyCoordinatesResponse getAgencyCoordinates(UUID agencyId) {
        String url = parcelServiceUrl + "/api/agencies/" + agencyId + "/coordinates";
        try {
            AgencyCoordinatesResponse response =
                    restTemplate.getForObject(url, AgencyCoordinatesResponse.class);
            if (response == null) {
                throw BusinessException.agencyNotFound(agencyId);
            }
            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch coordinates for agency {}: {}", agencyId, e.getMessage());
            throw BusinessException.agencyNotFound(agencyId);
        }
    }
}