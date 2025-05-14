package com.example.got.got.dto;

public class ApiResponseDto<T> {
    private boolean ok;
    private T data;
    private String message;

    public ApiResponseDto() {}

    public ApiResponseDto(boolean ok, T data, String message) {
        this.ok = ok;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponseDto<T> success(T data, String message) {
        return new ApiResponseDto<>(true, data, message);
    }

    public static <T> ApiResponseDto<T> error(String message) {
        return new ApiResponseDto<>(false, null, message);
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

