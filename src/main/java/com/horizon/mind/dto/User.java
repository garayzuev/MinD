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
    @EqualsAndHashCode.Exclude
    private final Set<Activity> preferredActivities;
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final Set<User> friends;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private String surname;
    @EqualsAndHashCode.Exclude
    private String email;
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String password;
    @EqualsAndHashCode.Exclude
    private byte[] image;


    @ConstructorProperties({"id", "foreignId", "preferredActivities", "friends", "name", "surname", "email", "password", "image"})
    public User(Long id, String foreignId, Set<Activity> preferredActivities, Set<User> friends, String name, String surname, String email, String password, byte[] image) {
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
