package com.asusoftware.user_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "places")
public class Place extends User {

    @Column(name = "location_id", nullable = false)
    private UUID locationId;  // Referință la locația din microserviciul de locații

    @Column(name = "place_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlaceType placeType; // Tipul afacerii ex. Club, bar, restaurant etc..

    @Embedded
    private ContactPerson contactPerson;
}