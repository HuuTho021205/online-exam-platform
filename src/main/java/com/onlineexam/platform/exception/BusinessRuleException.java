package com.onlineexam.platform.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    private final String errorMessage;

    public BusinessRuleException(String message, String errorMessage) {
        super(message);
        this.errorMessage = errorMessage;
    }
}
