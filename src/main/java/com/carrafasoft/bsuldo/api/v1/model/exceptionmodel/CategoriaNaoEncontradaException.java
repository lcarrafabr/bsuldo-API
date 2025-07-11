package com.carrafasoft.bsuldo.api.v1.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;

public class CategoriaNaoEncontradaException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de categoria com o código %s";

    public CategoriaNaoEncontradaException(String categoriaId) {

        super(String.format(NAO_EXISTE_CADASTRO_COM_ID, categoriaId));
    }
}
