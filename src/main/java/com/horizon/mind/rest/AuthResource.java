package com.horizon.mind.rest;

import com.github.scribejava.core.model.Token;
import com.horizon.mind.dto.User;
import com.horizon.mind.service.auth.IOAuthService;
import com.horizon.mind.service.db.DataBaseService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.horizon.mind.Helper.getCookie;
import static com.horizon.mind.Helper.invalidateCookie;

/**
 * Created by garayzuev@gmail.com on 19.06.2018.
 */
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final DataBaseService dataBase;
    private final Map<String, IOAuthService> servicesByName;

    @Autowired
    public AuthResource(DataBaseService dataBase, IOAuthService... services) {
        this.dataBase = dataBase;
        servicesByName = Arrays.stream(services).collect(Collectors.toMap(IOAuthService::getServiceName, s -> s));
    }

    @GetMapping("login/{service}")
    public ResponseEntity authenticate(@PathVariable String service) {
        IOAuthService authService = servicesByName.get(service);
        if (authService == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(authService.getAuthorizationUrl())
                .build();
    }

    @GetMapping("/login/")
    public ResponseEntity responseFromService(@RequestParam String code, @RequestParam String state) {
        long id = loginByService(code, state);

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create("/user/"))
                .headers(getCookie(id))
                .build();
    }

    private long loginByService(String code, String state) {
        if (Strings.isBlank(state)) {
            throw new IllegalArgumentException("State is empty!");
        }

        IOAuthService authService = servicesByName.get(state);
        if (authService == null) {
            throw new IllegalArgumentException("Don't found service by state " + state);
        }

        Token token = authService.getToken(code);
        String userId = authService.getUserId(token);
        Optional<User> user = dataBase.getUserByForeignId(userId);
        return user.isPresent() ? user.get().getId() : dataBase.addUser(authService.getUserById(token, userId));
    }

    @PostMapping("/login/")
    public ResponseEntity loginByEmail(@RequestBody Map<String, String> map) {
        String email = map.get("email");
        String pass = map.get("password");
        Optional<User> user = dataBase.getUserByEmail(email);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(user.get().getPassword(), pass)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity
                .ok()
                .headers(getCookie(user.get().getId()))
                .body(user);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(@CookieValue("user") long id) {
        Optional<User> user = dataBase.getUserById(id);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .headers(invalidateCookie())
                .body(user);
    }
}
