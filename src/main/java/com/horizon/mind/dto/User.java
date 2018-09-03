package com.horizon.mind.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Data
@Builder(toBuilder = true)
public class User {
    private final Long id;
    private final String foreignId;
    private final String name;
    private final String surname;
    private final String email;
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final Set<User> friends;
    private final Set<Activity> preferredActivities;
    private byte[] image;

    @ConstructorProperties({"id", "foreignId", "name", "surname", "email", "friends", "preferredActivities", "image"})
    public User(Long id, String foreignId, String name, String surname, String email, Set<User> friends, Set<Activity> preferredActivities, byte[] image) {
        this.id = id;
        this.foreignId = foreignId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.friends = friends == null ? new HashSet<>() : friends;
        this.preferredActivities = preferredActivities == null ? new HashSet<>() : preferredActivities;
        this.image = image;
    }
}
