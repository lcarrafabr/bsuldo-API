package com.carrafasoft.bsuldo.api.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;

public class LancamentoNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de lancamento com o código %d";

    public LancamentoNaoEncontradoException(String message) {
        super(message);
    }

    public LancamentoNaoEncontradoException(Long lancamentoId) {

        this(String.format(NAO_EXISTE_CADASTRO_COM_ID, lancamentoId));
    }
}
