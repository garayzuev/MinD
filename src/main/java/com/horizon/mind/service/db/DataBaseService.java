package com.horizon.mind.service.db;

import com.horizon.mind.dto.Activity;
import com.horizon.mind.dto.Place;
import com.horizon.mind.dto.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
public interface DataBaseService {
    long addUser(User data);

    long addActivity(Activity data);

    long addPlace(Place data);

    Optional<User> getUserById(long id);

    Optional<User> getUserByForeignId(String foreignId);

    Optional<Activity> getActivityById(long id);

    Optional<Place> getPlaceById(long id);

    List<Activity> getAllActivities();

    List<Place> getAllPlaces();

    Optional<User> getUserByEmail(String email);

    Optional<Activity> removeActivity(long id);

    Optional<Activity> removeActivityIfNoLinks(long id);

    Optional<Place> removePlace(long id);

    Optional<Place> removePlaceIfNoLinks(long id);

    Optional<User> removeUser(long id);
}
