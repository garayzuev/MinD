package com.horizon.mind.service.db.impl;

import com.horizon.mind.dto.Activity;
import com.horizon.mind.dto.Place;
import com.horizon.mind.dto.User;
import com.horizon.mind.service.db.DataBaseService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@Service
public class MockDataBaseService implements DataBaseService {
    private final AtomicLong activitiesCount = new AtomicLong(0);
    private final AtomicLong usersCount = new AtomicLong(0);
    private final AtomicLong placesCount = new AtomicLong(0);
    private final Map<Long, Activity> activities = new HashMap<>();
    private final Map<Long, Place> places = new HashMap<>();
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public long addUser(User data) {
        long id = usersCount.incrementAndGet();
        String pass = data.getPassword() == null || data.getPassword().isEmpty() ? "" : data.getPassword();
        User user = data.toBuilder()
                .id(id)
                .password(pass)
                .build();
        user.getFriends().parallelStream().map(User::getId).forEach(friendId -> users.get(friendId).getFriends().add(user));
        users.put(id, user);
        return id;
    }

    @Override
    public long addActivity(Activity data) {
        long id = activitiesCount.incrementAndGet();
        activities.put(id, data.toBuilder().id(id).build());
        return id;
    }

    @Override
    public long addPlace(Place data) {
        long id = placesCount.incrementAndGet();
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

    @Override
    public Optional<Activity> removeActivity(long id) {
        Activity activity = activities.remove(id);
        if (activity == null) {
            return Optional.empty();
        }
        activity.getPreferredPlaces().parallelStream().map(Place::getId).forEach(this::removePlaceIfNoLinks);
        users.values().parallelStream().forEach(u -> u.getPreferredActivities().remove(activity));
        return Optional.of(activity);
    }

    @Override
    public Optional<Activity> removeActivityIfNoLinks(long id) {
        Activity activity;
        if (users.values()
                .parallelStream()
                .map(User::getPreferredActivities)
                .flatMap(Collection::parallelStream)
                .map(Activity::getId)
                .noneMatch(aId -> aId == id)) {
            activity = activities.remove(id);
            activity.getPreferredPlaces().parallelStream().map(Place::getId).forEach(this::removePlaceIfNoLinks);
        } else {
            activity = activities.get(id);
        }
        return Optional.ofNullable(activity);
    }

    @Override
    public Optional<Place> removePlace(long id) {
        Place place = places.remove(id);
        if (place == null) {
            return Optional.empty();
        }
        activities.values().parallelStream().forEach(a -> a.getPreferredPlaces().remove(place));
        return Optional.of(place);
    }

    @Override
    public Optional<Place> removePlaceIfNoLinks(long id) {
        Place place = activities.values()
                .parallelStream()
                .map(Activity::getPreferredPlaces)
                .flatMap(Collection::parallelStream)
                .map(Place::getId)
                .noneMatch(pId -> pId == id)
                ? places.remove(id)
                : places.get(id);
        return Optional.ofNullable(place);
    }

    @Override
    public Optional<User> removeUser(long id) {
        User user = users.remove(id);
        if (user == null) {
            return Optional.empty();
        }
        user.getPreferredActivities().parallelStream().map(Activity::getId).forEach(this::removeActivityIfNoLinks);
        user.getFriends().parallelStream().forEach(u -> u.getFriends().remove(user));
        return Optional.of(user);
    }
}
