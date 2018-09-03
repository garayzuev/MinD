package com.horizon.mind.rest.impl;

import com.horizon.mind.db.DataBaseService;
import com.horizon.mind.dto.Activity;
import com.horizon.mind.dto.User;
import com.horizon.mind.rest.RestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public User add(User user) {
        long id = service.addUser(user);
        return service.getUserById(id).orElseThrow(IllegalArgumentException::new);
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User usr = add(user);
        return ResponseEntity
                .status(CREATED)
                .header("Set-Cookie", "user=" + Long.toString(usr.getId()) + "; Max-Age=63072000; Domain=localhost; HttpOnly; Path=/")
                .body(usr);
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
    public User getById(@PathVariable long id) {
        return service.getUserById(id).orElse(null);
    }

    @ResponseBody
    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public User getByCookie(@CookieValue("user") long id) {
        return service.getUserById(id).orElse(null);
    }

    @ResponseBody
    @PutMapping(value = "/friends/{friendId}")
    public User addFriendRelation(@CookieValue("user") long id, @PathVariable long friendId) {
        User user = service.getUserById(id).orElseThrow(IllegalArgumentException::new);
        service.getUserById(friendId).ifPresent(u -> {
            user.getFriends().add(u);
            u.getFriends().add(user);
        });
        return user;
    }

    @ResponseBody
    @GetMapping(value = "/friends", produces = APPLICATION_JSON_UTF8_VALUE)
    public Collection<User> getFriends(@CookieValue("user") long id) {
        return service.getUserById(id).orElseThrow(IllegalArgumentException::new).getFriends();
    }

    @ResponseBody
    @PutMapping(value = "/activities/{activityId}")
    public User addFavoriteActivity(@CookieValue("user") long id, @PathVariable long activityId) {
        User user = service.getUserById(id).orElseThrow(IllegalArgumentException::new);
        Optional<Activity> activity = service.getActivityById(activityId);
        activity.ifPresent(a -> user.getPreferredActivities().add(a));
        return user;
    }

    @ResponseBody
    @GetMapping(value = "/activities")
    public Collection<Activity> getActivities(@CookieValue("user") long id) {
        return service.getUserById(id).orElseThrow(IllegalArgumentException::new).getPreferredActivities();
    }

    @ResponseBody
    @PutMapping(value = "/image")
    public User uploadImage(@CookieValue("user") long id, @RequestBody byte[] image) {
        User user = service.getUserById(id).orElseThrow(IllegalArgumentException::new);
        user.setImage(image);
        return user;
    }
}
