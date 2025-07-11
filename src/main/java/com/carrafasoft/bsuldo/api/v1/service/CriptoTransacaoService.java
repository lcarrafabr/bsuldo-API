package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.CriptoTransacaoInput;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.CriptoTransacaoResponse;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.CriptoTransacao;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CriptoTransacaoService {

    List<CriptoTransacao> listar(String tokenId);

    CriptoTransacao findByCodigoAndTokenId(String codigoCriptoTransacao, String tokenId);

    CriptoTransacao cadastrarCriptoTransacao(CriptoTransacaoInput criptoTransacaoInput, String tokenId,
                                             HttpServletResponse response);

    CriptoTransacao atualizaCriptoTransacao(String codigoCriptoTransacao, CriptoTransacaoResponse criptoTransacaoInput, String tokenId);

    void removerCriptoTransacao(String codigoCritoTransacao);
}
