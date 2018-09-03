package com.horizon.mind.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Activity {
    private final Long id;
    private final String name;
    private final byte[] image;
    private List<Place> preferredPlaces;
}