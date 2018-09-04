package com.horizon.mind;

import org.springframework.http.HttpHeaders;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

public class Helper {
    private Helper() {
    }

    public static HttpHeaders getCookie(long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(SET_COOKIE, singletonList("user=" + Long.toString(id) + "; Max-Age=63072000; Domain=localhost; HttpOnly; Path=/"));
        return headers;
    }
}
