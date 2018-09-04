package com.horizon.mind.rest.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND, reason = "User is not found")
public class UserNotFoundException extends NotFoundException {
}
