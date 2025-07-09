package com.kientran.cinehub.exception; // Đặt trong gói 'exception'

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * @param message Thông báo lỗi.
     * @param cause Ngoại lệ gốc gây ra lỗi này.
     */
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}