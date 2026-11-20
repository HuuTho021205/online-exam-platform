package com.onlineexam.platform.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldValue) {
        super(String.format("%s not found with %s: %d", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    public ResourceNotFoundException(String resourceName, String fieldName, List<Long> fieldValues) {
        super(String.format("%s not found with %s: %d", resourceName, fieldName, fieldValues));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValues;
    }
}
