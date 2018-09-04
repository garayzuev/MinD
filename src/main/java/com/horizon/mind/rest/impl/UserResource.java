package com.horizon.mind.rest.impl;

import com.horizon.mind.dto.Activity;
import com.horizon.mind.dto.User;
import com.horizon.mind.rest.RestResource;
import com.horizon.mind.rest.exception.AccessDeniedException;
import com.horizon.mind.rest.exception.UserNotFoundException;
import com.horizon.mind.service.db.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.horizon.mind.Helper.getCookie;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
                .headers(getCookie(usr.getId()))
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
        return service.getUserById(id).orElseThrow(UserNotFoundException::new);
    }

    @ResponseBody
    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    public User getByCookie(@CookieValue("user") long id) {
        return service.getUserById(id).orElseThrow(UserNotFoundException::new);
    }

    @ResponseBody
    @PutMapping(value = "/friends/{friendId}")
    public User addFriendRelation(@CookieValue("user") long id, @PathVariable long friendId) {
        User user = service.getUserById(id).orElseThrow(UserNotFoundException::new);
        service.getUserById(friendId).ifPresent(u -> {
            user.getFriends().add(u);
            u.getFriends().add(user);
        });
        return user;
    }

    @ResponseBody
    @GetMapping(value = "/friends", produces = APPLICATION_JSON_UTF8_VALUE)
    public Collection<User> getFriends(@CookieValue("user") long id) {
        return service.getUserById(id).orElseThrow(UserNotFoundException::new).getFriends();
    }

    @ResponseBody
    @PutMapping(value = "/activities/{activityId}")
    public User addFavoriteActivity(@CookieValue("user") long id, @PathVariable long activityId) {
        User user = service.getUserById(id).orElseThrow(UserNotFoundException::new);
        Optional<Activity> activity = service.getActivityById(activityId);
        activity.ifPresent(a -> user.getPreferredActivities().add(a));
        return user;
    }

    @ResponseBody
    @GetMapping(value = "/activities")
    public Collection<Activity> getActivities(@CookieValue("user") long id) {
        return service.getUserById(id).orElseThrow(UserNotFoundException::new).getPreferredActivities();
    }

    @ResponseBody
    @PutMapping(value = "/image")
    public User uploadImage(@CookieValue("user") long id, @RequestBody byte[] image) {
        User user = service.getUserById(id).orElseThrow(UserNotFoundException::new);
        user.setImage(image);
        return user;
    }

    @ResponseBody
    @PutMapping(value = "/pass")
    public User changePassword(@CookieValue("user") long id, @RequestBody Map<String, String> passwords) {
        User user = service.getUserById(id).orElseThrow(UserNotFoundException::new);
        String oldPass = passwords.get("oldPassword");
        String newPass = passwords.get("newPassword");
        if (!Objects.equals(user.getPassword(), oldPass)) {
            throw new AccessDeniedException();
        }
        user.setPassword(newPass);
        return user;
    }

    @ResponseStatus(value = BAD_REQUEST, reason = "Can't add a user")
    @ExceptionHandler(IllegalArgumentException.class)
    private void creatingProblemResolver() {
    }
}
