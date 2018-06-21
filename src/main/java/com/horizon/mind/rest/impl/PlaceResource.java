package com.horizon.mind.rest.impl;

import com.horizon.mind.db.DataBaseService;
import com.horizon.mind.dto.Place;
import com.horizon.mind.rest.RestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * Created by garayzuev@gmail.com on 15.06.2018.
 */
@RestController
@RequestMapping("/place")
public class PlaceResource implements RestResource<Place> {
    private final DataBaseService service;

    @Autowired
    public PlaceResource(DataBaseService service) {
        this.service = service;
    }

    @Override
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public void add(@RequestBody Place place) {
        service.addPlace(place);
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/getAll", produces = APPLICATION_JSON_UTF8_VALUE)
    public List<Place> getAll() {
        return service.getAllPlaces();
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Place getById(@PathVariable("id") long id) {
        return service.getPlaceById(id).orElse(null);
    }
}
