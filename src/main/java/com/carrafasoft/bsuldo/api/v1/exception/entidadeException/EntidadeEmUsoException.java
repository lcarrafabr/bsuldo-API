package com.carrafasoft.bsuldo.api.v1.exception.entidadeException;

import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;

public class EntidadeEmUsoException extends NegocioException {

    public EntidadeEmUsoException(String message) {
        super(message);
    }
}
