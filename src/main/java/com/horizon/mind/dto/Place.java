package com.horizon.mind.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Place {

    @JsonProperty
    private final Long id;

    @JsonProperty
    private final String name;

    @JsonProperty
    //private byte[] image;
    private final String image;

    @JsonProperty
    private final Double latitude;

    @JsonProperty
    private final Double longitude;
}
