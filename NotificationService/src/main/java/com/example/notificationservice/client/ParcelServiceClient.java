package com.example.notificationservice.client;

import com.example.notificationservice.dto.old.ParcelSummaryResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ParcelService", path = "/logistics/api/v1/parcels")
public interface ParcelServiceClient {

    @GetMapping("/{parcelId}/summary")
    ParcelSummaryResponse getParcelSummary(@PathVariable("parcelId") String parcelId);

    @GetMapping("/api/v1/parcels/{parcelId}/owner")
    UUID getParcelOwner(@PathVariable("parcelId") String parcelId);
}
