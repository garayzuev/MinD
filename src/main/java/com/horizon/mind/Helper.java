package com.horizon.mind;

import org.springframework.http.HttpHeaders;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

public class Helper {
    private static final String COOKIE_TEMPLATE = "user=%s; Max-Age=63072000; Domain=%s; HttpOnly; Secure; Path=/";
    private static final String DOMAIN = "localhost";

    private Helper() {
    }

    public static HttpHeaders getCookie(long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(SET_COOKIE, prepareCookie(id));
        return headers;
    }

    public static HttpHeaders invalidateCookie() {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(SET_COOKIE, prepareCookie(null));
        return headers;
    }

    private static List<String> prepareCookie(Long id) {
        return singletonList(String.format(COOKIE_TEMPLATE, id, DOMAIN));
    }
}
