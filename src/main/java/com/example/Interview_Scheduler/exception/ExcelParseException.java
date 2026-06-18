package com.example.Interview_Scheduler.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ExcelParseException extends RuntimeException {
    public ExcelParseException(String message) {
        super(message);
    }
}