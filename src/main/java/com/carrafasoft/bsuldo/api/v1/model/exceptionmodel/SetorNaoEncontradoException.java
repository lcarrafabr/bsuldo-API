package com.carrafasoft.bsuldo.api.v1.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;

public class SetorNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de setor com o código %s";

    public SetorNaoEncontradoException(String message) {
        super(String.format(NAO_EXISTE_CADASTRO_COM_ID, message));
    }
}
