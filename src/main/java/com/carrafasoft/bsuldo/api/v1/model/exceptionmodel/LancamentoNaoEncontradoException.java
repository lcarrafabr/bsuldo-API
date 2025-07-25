package com.carrafasoft.bsuldo.api.v1.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;

public class LancamentoNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de lancamento com o código %d";
    public LancamentoNaoEncontradoException(String lancamentoId) {

        super(String.format(NAO_EXISTE_CADASTRO_COM_ID, lancamentoId));
    }
}
