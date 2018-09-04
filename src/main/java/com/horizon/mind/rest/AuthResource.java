package com.horizon.mind.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.horizon.mind.db.DataBaseService;
import com.horizon.mind.dto.User;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.scribejava.core.model.Verb.GET;

/**
 * Created by garayzuev@gmail.com on 19.06.2018.
 */
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final DataBaseService dataBase;

    @Value("${auth.fb.url}")
    private String fbUrl;
    @Value("${auth.fb.key}")
    private String fbKey;
    @Value("${auth.fb.secret}")
    private String fbSecret;
    @Value("${auth.callback}")
    private String callback;


    private Map<String, OAuth20Service> servicesByName;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public AuthResource(DataBaseService dataBase) {
        this.dataBase = dataBase;
    }

    @PostConstruct
    public void init() {
        HashMap<String, OAuth20Service> map = new HashMap<>();
        OAuth20Service fbService = new ServiceBuilder(fbKey)
                .apiSecret(fbSecret)
                .callback(callback)
                .build(FacebookApi.instance());
        map.put("facebook", fbService);
        servicesByName = Collections.unmodifiableMap(map);
    }

    @GetMapping("login/{service}")
    public ResponseEntity authenticate(@PathVariable String service) {
        OAuth20Service authService = servicesByName.get(service);
        if (authService == null)
            return ResponseEntity.badRequest().build();

        return ResponseEntity
                .status(HttpStatus.TEMPORARY_REDIRECT)
                .location(URI.create(authService.getAuthorizationUrl() + "&state=facebook&scope=email,user_friends"))
                .build();
    }

    @GetMapping("/login/")
    @SneakyThrows
    public ResponseEntity loginByService(@RequestParam String code, @RequestParam String state) {
        if (Strings.isBlank(state)) {
            return ResponseEntity.badRequest().build();
        }

        OAuth20Service authService = servicesByName.get(state);
        if (authService == null) {
            return ResponseEntity.badRequest().build();
        }

        OAuth2AccessToken token = authService.getAccessToken(code);

        String userId = getUserId(token, authService);

        Optional<User> user = dataBase.getUserByForeignId(userId);

        long id = user.isPresent() ? user.get().getId() : dataBase.addUser(getUserById(token, authService, userId));

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

    @SneakyThrows
    private User getUserById(OAuth2AccessToken token, OAuth20Service service, String id) {
        Response response = executeRequest(token, service, fbUrl + id + "?fields=email,first_name,last_name", GET);
        String body = IOUtils.toString(response.getStream(), Charset.defaultCharset());
        JsonNode json = mapper.readTree(body);
        User.UserBuilder userBuilder = User.builder()
                .foreignId(id)
                .email(json.get("email").asText())
                .name(json.get("first_name").asText())
                .surname(json.get("last_name").asText());

        response = executeRequest(token, service, fbUrl + id + "/picture?type=large", GET);
        byte[] image = IOUtils.toByteArray(response.getStream());
        return userBuilder.image(image).friends(getFriends(token, service, id)).build();
    }

    @SneakyThrows
    private Set<User> getFriends(OAuth2AccessToken token, OAuth20Service service, String id) {
        Response response = executeRequest(token, service, fbUrl + id + "/friends", GET);
        String body = IOUtils.toString(response.getStream(), Charset.defaultCharset());
        JsonNode json = mapper.readTree(body);

        return StreamSupport.stream(json.get("data").spliterator(), true)
                .map(j -> j.get("id"))
                .map(JsonNode::asText)
                .map(dataBase::getUserByForeignId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    private String getUserId(OAuth2AccessToken token, OAuth20Service service) {
        Response response = executeRequest(token, service, fbUrl + "me", GET);
        String body = IOUtils.toString(response.getStream(), Charset.defaultCharset());
        return mapper.readTree(body).get("id").asText();
    }

    @SneakyThrows
    private Response executeRequest(OAuth2AccessToken token, OAuth20Service service, String url, Verb method) {
        OAuthRequest request = new OAuthRequest(method, url);
        service.signRequest(token, request);
        return service.execute(request);
    }
}
