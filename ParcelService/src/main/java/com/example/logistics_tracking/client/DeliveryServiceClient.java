package com.example.logistics_tracking.client;

import com.example.logistics_tracking.dto.old.DeliveryBootstrapRequest;
import com.example.logistics_tracking.dto.old.DeliverySummaryResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "DeliveryService", path = "/logistics/api/v1/deliveries")
public interface DeliveryServiceClient {

    @PostMapping
    DeliverySummaryResponse createDelivery(@RequestBody DeliveryBootstrapRequest request);

    @GetMapping("/parcel/{parcelId}")
    DeliverySummaryResponse getByParcelId(@PathVariable("parcelId") UUID parcelId);
}
