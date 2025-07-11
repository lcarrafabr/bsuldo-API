package com.carrafasoft.bsuldo.api.v1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NegocioException extends RuntimeException{

    public NegocioException(String message) {
        super(message);
    }

    public NegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}
