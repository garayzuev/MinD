package com.horizon.mind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Place {
    private final Long id;
    private final String name;
    private final byte[] image;
    private final Double latitude;
    private final Double longitude;
}
