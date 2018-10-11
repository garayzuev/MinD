package com.horizon.mind.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Data
@Builder(toBuilder = true)
public class Activity {
    private final Long id;
    @EqualsAndHashCode.Exclude
    private final String name;
    @EqualsAndHashCode.Exclude
    private byte[] image;
    @EqualsAndHashCode.Exclude
    private Set<Place> preferredPlaces;

    @ConstructorProperties({"id", "name", "image", "preferredPlaces"})
    public Activity(Long id, String name, byte[] image, Set<Place> preferredPlaces) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.preferredPlaces = preferredPlaces == null ? new HashSet<>() : preferredPlaces;
    }
}