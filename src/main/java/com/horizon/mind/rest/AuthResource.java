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
    public ResponseEntity loginByService(@RequestParam String code, @RequestParam String state) {
        if (Strings.isBlank(state)) {
            return ResponseEntity.badRequest().build();
        }

        IOAuthService authService = servicesByName.get(state);
        if (authService == null) {
            return ResponseEntity.badRequest().build();
        }

        Token token = authService.getToken(code);

        String userId = authService.getUserId(token);

        Optional<User> user = dataBase.getUserByForeignId(userId);

        long id = user.isPresent() ? user.get().getId() : dataBase.addUser(authService.getUserById(token, userId));

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create("/user/"))
                .headers(getCookie(id))
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
        if (!Objects.equals(user.get().getPassword(), pass)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity
                .ok()
                //.header("Set-Cookie", "user=" + Long.toString(user.get().getId()) + "; Max-Age=63072000; Domain=localhost; HttpOnly; Path=/")
                .headers(getCookie(user.get().getId()))
                .body(user);
    }
}
