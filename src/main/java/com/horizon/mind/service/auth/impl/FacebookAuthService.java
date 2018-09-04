package com.horizon.mind.service.auth.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.horizon.mind.dto.User;
import com.horizon.mind.service.auth.OAuthService;
import com.horizon.mind.service.db.DataBaseService;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.scribejava.core.model.Verb.GET;

@Service
public class FacebookAuthService implements OAuthService {
    private static final String SERVICE_NAME = "facebook";
    private final OAuth20Service fbService;
    private final ObjectMapper mapper;
    private final DataBaseService dataBase;
    private final URI authorizationUrl;
    private String fbUrl;

    @Autowired
    public FacebookAuthService(DataBaseService dataBase,
                               @Value("${auth.fb.url}") String fbUrl,
                               @Value("${auth.fb.key}") String fbKey,
                               @Value("${auth.fb.secret}") String fbSecret,
                               @Value("${auth.callback}") String callback) {
        fbService = new ServiceBuilder(fbKey)
                .apiSecret(fbSecret)
                .callback(callback)
                .build(FacebookApi.instance());
        this.fbUrl = fbUrl;
        mapper = new ObjectMapper();
        this.dataBase = dataBase;
        authorizationUrl = URI.create(fbService.getAuthorizationUrl() + "&state=facebook&scope=email,user_friends");
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    @SneakyThrows
    public OAuth2AccessToken getToken(String code) {
        return fbService.getAccessToken(code);
    }

    @Override
    public URI getAuthorizationUrl() {
        return authorizationUrl;
    }

    @SneakyThrows
    public User getUserById(OAuth2AccessToken token, String id) {
        Response response = executeRequest(token, fbService, fbUrl + id + "?fields=email,first_name,last_name", GET);
        String body = IOUtils.toString(response.getStream(), Charset.defaultCharset());
        JsonNode json = mapper.readTree(body);
        User.UserBuilder userBuilder = User.builder()
                .foreignId(id)
                .email(json.get("email").asText())
                .name(json.get("first_name").asText())
                .surname(json.get("last_name").asText());

        response = executeRequest(token, fbService, fbUrl + id + "/picture?type=large", GET);
        byte[] image = IOUtils.toByteArray(response.getStream());
        return userBuilder.image(image).friends(getFriends(token, id)).build();
    }

    @SneakyThrows
    private Set<User> getFriends(OAuth2AccessToken token, String id) {
        Response response = executeRequest(token, fbService, fbUrl + id + "/friends", GET);
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
    public String getUserId(OAuth2AccessToken token) {
        Response response = executeRequest(token, fbService, fbUrl + "me", GET);
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
