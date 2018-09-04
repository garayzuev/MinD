package com.horizon.mind.rest.impl;

import com.horizon.mind.dto.Place;
import com.horizon.mind.rest.RestResource;
import com.horizon.mind.rest.exception.NotFoundException;
import com.horizon.mind.service.db.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
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
    @ResponseBody
    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public Place add(@RequestBody Place place) {
        long id = service.addPlace(place);
        return service.getPlaceById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/getAll", produces = APPLICATION_JSON_UTF8_VALUE)
    public Collection<Place> getAll() {
        return service.getAllPlaces();
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    public Place getById(@PathVariable("id") long id) {
        return service.getPlaceById(id).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(value = BAD_REQUEST, reason = "Can't add a user")
    @ExceptionHandler(IllegalArgumentException.class)
    private void creatingProblemResolver() {
    }
}
