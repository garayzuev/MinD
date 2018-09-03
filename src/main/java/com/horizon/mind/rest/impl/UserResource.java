package com.horizon.mind.rest.impl;

import com.horizon.mind.db.DataBaseService;
import com.horizon.mind.dto.User;
import com.horizon.mind.rest.RestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * Created by garayzuev@gmail.com on 18.06.2018.
 */
@RestController
@RequestMapping("/user")
public class UserResource implements RestResource<User> {
    private final DataBaseService service;

    @Autowired
    public UserResource(DataBaseService service) {
        this.service = service;
    }

    @Override
    @ResponseStatus(CREATED)
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public void add(@RequestBody User user) {
        service.addUser(user);
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/getAll", produces = TEXT_HTML_VALUE)
    public List<User> getAll() {
        throw new UnsupportedOperationException("Method getAll for Users isn't supported");
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    public User getById(@PathVariable("id") long id) {
        return service.getUserById(id).orElse(null);
    }

    @ResponseBody
    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public User getByCookie(@CookieValue("user") long id){
        return service.getUserById(id).orElse(null);
    }
}
