package com.law.convertilovelaw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class HistoryNotFound extends RuntimeException {
    public HistoryNotFound(String message) {
        super(message);
    }
}
