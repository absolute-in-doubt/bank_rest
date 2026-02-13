package com.example.bankcards.dto;

public class ErrorResponseDto {
    private Object error;

    public ErrorResponseDto(Object error){
        this.error = error;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
