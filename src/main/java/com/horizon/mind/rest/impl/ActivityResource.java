package com.horizon.mind.rest.impl;

import com.horizon.mind.dto.Activity;
import com.horizon.mind.dto.Place;
import com.horizon.mind.rest.RestResource;
import com.horizon.mind.rest.exception.NotFoundException;
import com.horizon.mind.service.db.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created by garayzuev@gmail.com on 18.06.2018.
 */
@RestController
@RequestMapping("/activity")
public class ActivityResource implements RestResource<Activity> {
    private final DataBaseService service;

    @Autowired
    public ActivityResource(DataBaseService service) {
        this.service = service;
    }

    @Override
    @ResponseBody
    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public Activity add(@RequestBody Activity activity) {
        long id = service.addActivity(activity);
        return service.getActivityById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/getAll", produces = APPLICATION_JSON_UTF8_VALUE)
    public Collection<Activity> getAll() {
        return service.getAllActivities();
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Activity getById(@PathVariable long id) {
        return service.getActivityById(id).orElseThrow(NotFoundException::new);
    }

    @ResponseBody
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Activity removeActivity(@PathVariable long id) {
        return service.removeActivity(id).orElseThrow(NotFoundException::new);
    }

    @ResponseBody
    @PutMapping(value = "/{id}/place/{placeId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Activity addPlace(@PathVariable long id, @PathVariable long placeId) {
        Activity activity = service.getActivityById(id).orElseThrow(NotFoundException::new);
        service.getPlaceById(placeId).ifPresent(p -> activity.getPreferredPlaces().add(p));
        return activity;
    }

    @ResponseBody
    @PostMapping(value = "/{id}/place", produces = APPLICATION_JSON_UTF8_VALUE)
    public Activity addPlace(@PathVariable long id, @RequestBody Place place) {
        Activity activity = service.getActivityById(id).orElseThrow(NotFoundException::new);
        long placeId = service.addPlace(place);
        service.getPlaceById(placeId).ifPresent(p -> activity.getPreferredPlaces().add(p));
        return activity;
    }

    @ResponseBody
    @DeleteMapping(value = "/{id}/place/{placeId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Optional<Place> removePlace(@PathVariable long id, @PathVariable long placeId) {
        Activity activity = service.getActivityById(id).orElseThrow(NotFoundException::new);
        if (activity.getPreferredPlaces().parallelStream().map(Place::getId).noneMatch(pId -> pId == placeId))
            return Optional.empty();
        service.getPlaceById(placeId).ifPresent(p -> activity.getPreferredPlaces().remove(p));
        return service.removePlaceIfNoLinks(placeId);
    }

    @ResponseBody
    @PutMapping(value = "/{id}/image")
    public Activity uploadImage(@PathVariable long id, @RequestBody byte[] image) {
        Activity activity = service.getActivityById(id).orElseThrow(NotFoundException::new);
        activity.setImage(image);
        return activity;
    }

    @ResponseStatus(value = BAD_REQUEST, reason = "Can't add an activity")
    @ExceptionHandler(IllegalArgumentException.class)
    private void creatingProblemResolver() {
    }
}
