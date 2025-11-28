package com.example.device_service.handlers.exceptions;

public class ExceptionHandlerResponseDTO {
    private int status;
    private String message;

    public ExceptionHandlerResponseDTO() {
    }

    public ExceptionHandlerResponseDTO(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}