package com.example.paymentservice.client;

import com.example.paymentservice.dto.ParcelSummaryResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ParcelService", path = "/api/v1/parcels")
public interface ParcelServiceClient {

    @GetMapping("/{parcelId}/summary")
    ParcelSummaryResponse getParcelSummary(@PathVariable("parcelId") UUID parcelId);
}
