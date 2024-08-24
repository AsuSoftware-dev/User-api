package com.asusoftware.user_api.client;

import com.asusoftware.user_api.client.dto.LocationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "location-service", url = "${location.service.url}")
public interface LocationClient {

    @GetMapping("/locations/{id}")
    LocationDto getLocationById(@PathVariable("id") UUID locationId);
}

