package com.carrafasoft.bsuldo.api.v1.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;

public class OrigemNaoEncontradaException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_ORIGEM_COM_ID = "Não existe cadastro de origem com o código %s";

    public OrigemNaoEncontradaException(String message) {
        super(String.format(NAO_EXISTE_ORIGEM_COM_ID, message));
    }
}
