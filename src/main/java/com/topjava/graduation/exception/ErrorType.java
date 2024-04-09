package com.topjava.graduation.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    NOT_FOUND("Wrong data in request", HttpStatus.NOT_FOUND),
    BAD_DATA("Wrong data", HttpStatus.UNPROCESSABLE_ENTITY),
    DATA_CONFLICT("DB data conflict", HttpStatus.CONFLICT),
    VOTING_RESTRICTIONS("Limiting changes", HttpStatus.UNPROCESSABLE_ENTITY),
    BAD_REQUEST("Bad request", HttpStatus.UNPROCESSABLE_ENTITY),
    AUTH_ERROR("Authorization error", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("Request unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Request forbidden", HttpStatus.FORBIDDEN),
    APP_ERROR("Application error", HttpStatus.INTERNAL_SERVER_ERROR);

    public final String title;
    public final HttpStatus status;

    ErrorType(String title, HttpStatus status) {
        this.title = title;
        this.status = status;
    }
}