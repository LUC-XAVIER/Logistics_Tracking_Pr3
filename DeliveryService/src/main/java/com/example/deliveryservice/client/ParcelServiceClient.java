package com.example.deliveryservice.client;

import com.example.deliveryservice.dto.AgencyCoordinatesResponse;
import com.example.deliveryservice.dto.AvailableParcelResponse;
import com.example.deliveryservice.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "ParcelService", path = "/logistics/api/v1/agencies")
public interface ParcelServiceClient {

  @GetMapping("/{agencyId}/coordinates")
  AgencyCoordinatesResponse getAgencyCoordinates(@PathVariable("agencyId") UUID agencyId);

  @GetMapping("/v1/parcels/available")
  List<AvailableParcelResponse> getAvailableParcels(
    @RequestParam("sourceAgencyId") UUID sourceAgencyId,
    @RequestParam("destAgencyId") UUID destAgencyId
  );

  @Component
  @Slf4j
  class ParcelServiceClientFallback implements ParcelServiceClient {

    @Override
    public AgencyCoordinatesResponse getAgencyCoordinates(UUID agencyId) {
      log.error("Failed to fetch coordinates for agency {}", agencyId);
      throw BusinessException.agencyNotFound(agencyId);
    }

    @Override
    public List<AvailableParcelResponse> getAvailableParcels(UUID sourceAgencyId, UUID destAgencyId) {
      log.error("Failed to fetch available parcels for route {} -> {}", sourceAgencyId, destAgencyId);
      return Collections.emptyList();
    }
  }
}
