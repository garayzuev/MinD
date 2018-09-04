package com.horizon.mind.rest;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.horizon.mind.dto.User;
import com.horizon.mind.service.auth.OAuthService;
import com.horizon.mind.service.db.DataBaseService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by garayzuev@gmail.com on 19.06.2018.
 */
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final DataBaseService dataBase;
    private final Map<String, OAuthService> servicesByName;

    @Autowired
    public AuthResource(DataBaseService dataBase, OAuthService... services) {
        this.dataBase = dataBase;
        servicesByName = Arrays.stream(services).collect(Collectors.toMap(OAuthService::getServiceName, s -> s));
    }

    @GetMapping("login/{service}")
    public ResponseEntity authenticate(@PathVariable String service) {
        OAuthService authService = servicesByName.get(service);
        if (authService == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(authService.getAuthorizationUrl())
                .build();
    }

    @GetMapping("/login/")
    public ResponseEntity loginByService(@RequestParam String code, @RequestParam String state) {
        if (Strings.isBlank(state)) {
            return ResponseEntity.badRequest().build();
        }

        OAuthService authService = servicesByName.get(state);
        if (authService == null) {
            return ResponseEntity.badRequest().build();
        }

        OAuth2AccessToken token = authService.getToken(code);

        String userId = authService.getUserId(token);

        Optional<User> user = dataBase.getUserByForeignId(userId);

        long id = user.isPresent() ? user.get().getId() : dataBase.addUser(authService.getUserById(token, userId));

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create("/user/"))
                .header("Set-Cookie", "user=" + Long.toString(id) + "; Max-Age=63072000; Domain=localhost; HttpOnly; Path=/")
                .build();
    }

    @PostMapping("/login/")
    public ResponseEntity loginByEmail(@RequestBody Map<String, String> map) {
        String email = map.get("email");
        String pass = map.get("password");
        Optional<User> user = dataBase.getUserByEmail(email);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        if (!user.get().getPassword().equals(pass)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create("/user/"))
                .header("Set-Cookie", "user=" + Long.toString(user.get().getId()) + "; Max-Age=63072000; Domain=localhost; HttpOnly; Path=/")
                .build();
    }
}
