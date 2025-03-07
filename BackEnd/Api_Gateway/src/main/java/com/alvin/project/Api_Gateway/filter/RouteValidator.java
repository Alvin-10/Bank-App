package com.alvin.project.Api_Gateway.filter;


import java.util.Arrays;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
@Component
public class RouteValidator {
    public static final String[] OPEN_API_ENDPOINTS = {
            "/auth/register", "/auth/authenticate", "/eureka"
    };

    public Predicate<ServerHttpRequest> isSecured = request -> {
        return Arrays.stream(OPEN_API_ENDPOINTS)
                .noneMatch(uri -> request.getURI().getPath().contains(uri));
    };
}