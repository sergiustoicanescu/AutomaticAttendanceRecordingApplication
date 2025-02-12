package com.example.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
public class CookieService {

    public Optional<String> findCookieValue(String name, HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public void clearCookie(String name, HttpServletResponse response, String domain) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(0);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }
}
