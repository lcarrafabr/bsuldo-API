package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRVResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRvInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutosRVInputUpdateRerpesentation;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;

import javax.servlet.http.HttpServletResponse;

public interface ProdutoRendaVariavelService {

    ProdutosRendaVariavel findByCodigoProdutoRVAndTokenId(String codigoProdutoRV, String tokenId);

    ProdutosRendaVariavel cadastrarAutomaticoRV(String ticker, String tokenApi, HttpServletResponse response, String tokenId);

    ProdutosRendaVariavel preparaCadastroProdutoRVManual(ProdutoRvInputRepresentation produtosRendaVariavel, HttpServletResponse response,String tokenId);

    ProdutoRVResponseRepresentation atualizarProdutoRV(String codigo, ProdutosRVInputUpdateRerpesentation produtosRVInputUpdateRerpesentation, String tokenId);

    void removerProduto(String codigoProdutoRv);
}
