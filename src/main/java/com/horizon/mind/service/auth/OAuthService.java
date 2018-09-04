package com.horizon.mind.service.auth;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.horizon.mind.dto.User;

import java.net.URI;

public interface OAuthService {
    String getUserId(OAuth2AccessToken token);

    User getUserById(OAuth2AccessToken token, String id);

    String getServiceName();

    OAuth2AccessToken getToken(String code);

    URI getAuthorizationUrl();
}
