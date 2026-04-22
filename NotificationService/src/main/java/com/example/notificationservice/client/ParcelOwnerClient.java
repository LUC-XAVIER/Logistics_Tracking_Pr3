package com.example.notificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "ParcelService", url = "${services.parcel-service.url}")
public interface ParcelOwnerClient {

    @GetMapping("/api/parcels/{parcelId}/owner")
    UUID getParcelOwner(@PathVariable("parcelId") String parcelId);
}