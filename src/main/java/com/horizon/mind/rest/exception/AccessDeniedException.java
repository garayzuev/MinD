package com.horizon.mind.rest.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(value = FORBIDDEN, reason = "Access denied")
public class AccessDeniedException extends RuntimeException {
}
