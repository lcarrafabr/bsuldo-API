package com.carrafasoft.bsuldo.api.model.exceptionmodel;

import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import org.springframework.dao.DataIntegrityViolationException;

public class MetodoDeCobrancaNaoEncontradoException extends EntidadeNaoEncontradaException {

    public static final String NAO_EXISTE_CADASTRO_COM_ID = "Não existe cadastro de método de cobrança com o código %s";

    public MetodoDeCobrancaNaoEncontradoException(String metodoCobrancaId) {

        super(String.format(NAO_EXISTE_CADASTRO_COM_ID, metodoCobrancaId));
    }
}
