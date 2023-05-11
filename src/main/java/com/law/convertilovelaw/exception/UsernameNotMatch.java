package com.law.convertilovelaw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsernameNotMatch extends RuntimeException{
    public UsernameNotMatch(String message) {
        super(message);
    }
}
