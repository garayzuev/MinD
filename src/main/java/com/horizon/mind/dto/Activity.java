package com.horizon.mind.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Activity {
    @JsonProperty
    private final Long id;

    @JsonProperty(required = true)
    private final String name;

    @JsonProperty(required = true)
    private final byte[] image;

    @JsonProperty
    List<Place> preferredPlaces;
}