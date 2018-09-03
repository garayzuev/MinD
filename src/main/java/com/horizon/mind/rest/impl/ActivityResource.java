package com.horizon.mind.rest.impl;

import com.horizon.mind.db.DataBaseService;
import com.horizon.mind.dto.Activity;
import com.horizon.mind.rest.RestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

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
        return service.getActivityById(id).orElse(null);
    }

    @ResponseBody
    @PutMapping(value = "/{id}/place/{placeId}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Activity addPlace(@PathVariable long id, @PathVariable long placeId) {
        Activity activity = service.getActivityById(id).orElseThrow(IllegalArgumentException::new);
        service.getPlaceById(placeId).ifPresent(p -> activity.getPreferredPlaces().add(p));
        return activity;
    }
}
