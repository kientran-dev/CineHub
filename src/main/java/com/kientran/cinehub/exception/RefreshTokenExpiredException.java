package com.kientran.cinehub.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.FORBIDDEN) // Mã lỗi 403 Forbidden
public class RefreshTokenExpiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
