package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemInput;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;

import javax.servlet.http.HttpServletResponse;

public interface OrigemService {


    Origens buscaOrigemPorIdAndToken(String codigoOrigem, String tokenId);

    Origens cadastrarOrigem(OrigemInput origemInput, String tokenId, HttpServletResponse response);

    Origens atualizarOrigem(String codigoOrigem, OrigemInput origemInput, String tokenId);
}
