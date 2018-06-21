package com.horizon.mind.rest.impl;

import com.horizon.mind.db.DataBaseService;
import com.horizon.mind.dto.Activity;
import com.horizon.mind.rest.RestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public void add(@RequestBody Activity activity) {
        service.addActivity(activity);
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/getAll", produces = APPLICATION_JSON_UTF8_VALUE)
    public List<Activity> getAll() {
        return service.getAllActivities();
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Activity getById(@PathVariable("id") long id) {
        return service.getActivityById(id).orElse(null);
    }
}
