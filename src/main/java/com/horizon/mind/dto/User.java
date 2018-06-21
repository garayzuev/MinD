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
public class User {
    @JsonProperty
    private final Long id;

    @JsonProperty
    private final String foreignId;

    @JsonProperty
    private final String name;

    @JsonProperty
    private final String surname;

    @JsonProperty
    private final String email;

    @JsonProperty
    private final byte[] image;

    @JsonProperty
    private final List<User> friends;

    @JsonProperty
    private final List<Activity> preferredActivities;
}
