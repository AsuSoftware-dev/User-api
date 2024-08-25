package com.asusoftware.user_api.client;

import com.asusoftware.user_api.client.dto.LocationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "location-service", url = "${location.service.url}")
public interface LocationClient {

    @PostMapping
    LocationDto createLocation(@RequestBody LocationDto locationDto);

    @GetMapping("/{id}")
    LocationDto getLocationById(@PathVariable("id") UUID locationId);

    @PutMapping("/{id}")
    LocationDto updateLocation(@PathVariable("id") UUID locationId, @RequestBody LocationDto updatedLocation);

    @DeleteMapping("/{id}")
    void deleteLocation(@PathVariable("id") UUID locationId);
}

