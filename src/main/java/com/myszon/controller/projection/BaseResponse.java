package com.myszon.controller.projection;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Getter;

@Introspected @Getter @Builder
public class BaseResponse<T> {
    int status;
    String error;
    T entity;
}
