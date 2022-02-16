package com.myszon.controller.projection;

public class BaseResponse<T> {
    int status;
    String message;
    T entity;

    public BaseResponse(int status, String message, T entity) {
        this.status = status;
        this.message = message;
        this.entity = entity;
    }

    public BaseResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getEntity() {
        return entity;
    }
}
