package com.horizon.mind.db.impl;

import com.horizon.mind.db.DataBaseService;
import com.horizon.mind.dto.Activity;
import com.horizon.mind.dto.Place;
import com.horizon.mind.dto.User;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Service
public class MockDataBaseService implements DataBaseService {
    private final Map<Long, Activity> activities = new HashMap<>();
    private final Map<Long, Place> places = new HashMap<>();
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public long addUser(User data) {
        long id = data.hashCode();
        User user = data.toBuilder().id(id).build();
        user.getFriends().parallelStream().map(User::getId).forEach(fid -> users.get(fid).getFriends().add(user));
        users.put(id, user);
        return id;
    }

    @Override
    public long addActivity(Activity data) {
        long id = data.hashCode();
        activities.put(id, data.toBuilder().id(id).build());
        return id;
    }

    @Override
    public long addPlace(Place data) {
        long id = data.hashCode();
        places.put(id, data.toBuilder().id(id).build());
        return id;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> getUserByForeignId(String foreignId) {
        return users.values().parallelStream().filter(user -> Objects.equals(user.getForeignId(), foreignId)).findFirst();
    }

    @Override
    public Optional<Activity> getActivityById(long id) {
        return Optional.ofNullable(activities.get(id));
    }

    @Override
    public Optional<Place> getPlaceById(long id) {
        return Optional.ofNullable(places.get(id));
    }

    @Override
    public List<Activity> getAllActivities() {
        return new ArrayList<>(activities.values());
    }

    @Override
    public List<Place> getAllPlaces() {
        return new ArrayList<>(places.values());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.values().parallelStream().filter(u -> u.getEmail().equals(email)).findFirst();
    }
}
