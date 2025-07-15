package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendoInput;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendosInputUpdate;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto.ControleDividendosCadastroCombobox;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ControleDividendoService {

    List<ControleDividendos> listarTodosOsDividendos(String tokenId, String ticker, String tipoRecebimento,
                                                     String dataReferencia, String dataPagamento);

    ControleDividendos findByCodigoControleDivAndTokenId(String codigoControleDividendo, String tokenId);

    List<ControleDividendosCadastroCombobox> buscaControleDividendosCombobox(String tokenId);

    ControleDividendos atualizarControleDividendos(String codigo, ControleDividendosInputUpdate controleDividendos, String tokenId);

    ControleDividendos cadastrarControleDividendo(ControleDividendoInput controleDividendos, HttpServletResponse response, String tokenId);


    void removerControleDividendo(String codigo);

    void atualizaAtatusDividendoAtivo(String codigo, Boolean usado);
}
