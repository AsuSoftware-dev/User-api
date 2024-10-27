package com.asusoftware.user_api.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class LocationIdsRequest {
    private List<UUID> locationIds;
}
