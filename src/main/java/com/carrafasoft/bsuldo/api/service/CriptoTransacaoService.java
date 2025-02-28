package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.mapper.criptomoeda.CriptoTransacaoInput;
import com.carrafasoft.bsuldo.api.model.criptomoedas.CriptoTransacao;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CriptoTransacaoService {

    List<CriptoTransacao> listar(String tokenId);

    CriptoTransacao findByCodigoAndTokenId(String codigoCriptoTransacao, String tokenId);

    CriptoTransacao cadastrarCriptoTransacao(CriptoTransacaoInput criptoTransacaoInput, String tokenId,
                                             HttpServletResponse response);
}
