package com.horizon.mind.service.auth;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.horizon.mind.dto.User;
import lombok.SneakyThrows;

import java.net.URI;

public interface IOAuthService {
    String getUserId(Token token);

    User getUserById(Token token, String id);

    String getServiceName();

    URI getAuthorizationUrl();

    Token getToken(String code);

    @SneakyThrows
    default Response executeRequest(Token token, OAuthService service, String url, Verb method) {
        OAuthRequest request = new OAuthRequest(method, url);
        signRequest(token, request);
        return service.execute(request);
    }

    void signRequest(Token token, OAuthRequest request);
}
