package com.example.Interview_Scheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BatchException extends RuntimeException {
    public BatchException(String message) {
        super(message);
    }
}