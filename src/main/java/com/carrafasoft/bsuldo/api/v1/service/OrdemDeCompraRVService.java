package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraInputReppresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface OrdemDeCompraRVService {

    List<OrdensDeCompra> findAll(String tokenId);

    OrdensDeCompra buscaPorCodidoOrdemDeCompraAndTokenId(String codigoOrdemDeCompra, String tokenId);

    PrecoAtualCota consultaPrecoAtualCota(String ticker, String apiToken);

    OrdensDeCompra cadastrarOrdemDeCompraVenda(OrdemDeCompraInputReppresentation ordemCompra, HttpServletResponse response, String tokenId);

    void deleteByCodigo(String codigo);

    OrdensDeCompra atualizarOrdemCompraVenda(String codigo, OrdemDeCompraInputUpdateRepresentation ordensDeCompra, String tokenId);
}
