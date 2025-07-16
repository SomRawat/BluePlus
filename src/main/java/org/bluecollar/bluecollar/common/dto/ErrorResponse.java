package org.bluecollar.bluecollar.common.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final int statusCode;
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}