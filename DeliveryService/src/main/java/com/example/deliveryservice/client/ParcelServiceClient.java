package com.example.deliveryservice.client;

import com.example.deliveryservice.dto.ParcelStatusUpdateRequest;
import com.example.deliveryservice.dto.ParcelSummaryResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ParcelService", path = "/api/v1/parcels")
public interface ParcelServiceClient {

    @GetMapping("/{parcelId}/summary")
    ParcelSummaryResponse getParcelSummary(@PathVariable("parcelId") UUID parcelId);

    @PatchMapping("/{parcelId}/status")
    void updateParcelStatus(@PathVariable("parcelId") UUID parcelId,
                            @RequestBody ParcelStatusUpdateRequest request);
}
