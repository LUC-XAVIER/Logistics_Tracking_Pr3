package com.example.notificationservice.client;

import com.example.notificationservice.dto.DeliverySummaryResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "DeliveryService", path = "/api/v1/deliveries")
public interface DeliveryServiceClient {

    @GetMapping("/parcel/{parcelId}")
    DeliverySummaryResponse getByParcelId(@PathVariable("parcelId") UUID parcelId);
}
