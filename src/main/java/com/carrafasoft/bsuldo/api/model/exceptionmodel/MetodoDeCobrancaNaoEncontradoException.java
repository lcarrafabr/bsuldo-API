package com.carrafasoft.bsuldo.api.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;

public class MetodoDeCobrancaNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de categoria com o código %d";

    public MetodoDeCobrancaNaoEncontradoException(String message) {
        super(message);
    }

    public MetodoDeCobrancaNaoEncontradoException(Long meetodoCobrancaId) {

        this(String.format(NAO_EXISTE_CADASTRO_COM_ID, meetodoCobrancaId));
    }
}
