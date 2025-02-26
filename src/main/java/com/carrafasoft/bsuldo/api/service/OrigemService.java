package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;

public interface OrigemService {


    Origens buscaOrigemPorIdAndToken(String codigoOrigem, String tokenId);
}
