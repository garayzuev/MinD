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
    @EqualsAndHashCode.Exclude
    private final Long id;
    private final String name;
    @EqualsAndHashCode.Exclude
    private final byte[] image;
    private final Double latitude;
    private final Double longitude;

    @ConstructorProperties({"id", "name", "image", "latitude", "longitude"})
    public Place(Long id, String name, byte[] image, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
