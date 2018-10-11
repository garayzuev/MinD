package com.horizon.mind.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Data
@Builder(toBuilder = true)
public class Place {
    private final Long id;
    @EqualsAndHashCode.Exclude
    private final String name;
    @EqualsAndHashCode.Exclude
    private final Double latitude;
    @EqualsAndHashCode.Exclude
    private final Double longitude;
    @EqualsAndHashCode.Exclude
    private byte[] image;

    @ConstructorProperties({"id", "name", "latitude", "longitude", "image"})
    public Place(Long id, String name, Double latitude, Double longitude, byte[] image) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
