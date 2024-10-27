package com.asusoftware.user_api.repository;

import com.asusoftware.user_api.client.dto.LocationDto;
import com.asusoftware.user_api.model.Place;
import com.asusoftware.user_api.model.PlaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaceRepository extends JpaRepository<Place, UUID> {
    List<Place> findByPlaceType(PlaceType placeType);
    @Query("SELECT p FROM Place p WHERE p.locationId IN :locationIds")
    List<Place> findPlacesByLocationIds(@Param("locationIds") List<UUID> locationIds);
    //List<Place> findByLocation(LocationDto location);
}
