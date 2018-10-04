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
    @EqualsAndHashCode.Exclude
    private final Long id;
    private final String foreignId;
    private final String name;
    private final String surname;
    private String email;
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final Set<User> friends;
    @JsonIgnore
    @ToString.Exclude
    private String password;
    private final Set<Activity> preferredActivities;
    @EqualsAndHashCode.Exclude
    private byte[] image;


    @ConstructorProperties({"id", "foreignId", "name", "surname", "email", "friends", "password", "preferredActivities", "image"})
    public User(Long id, String foreignId, String name, String surname, String email, Set<User> friends, String password, Set<Activity> preferredActivities, byte[] image) {
        this.id = id;
        this.foreignId = foreignId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.friends = friends == null ? new HashSet<>() : friends;
        this.preferredActivities = preferredActivities == null ? new HashSet<>() : preferredActivities;
        this.image = image;
    }
}
