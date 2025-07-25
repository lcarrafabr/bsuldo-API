package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.OrigemInput;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.OrigemUpdateInput;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.Origens;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface OrigemService {


    Origens buscaOrigemPorIdAndToken(String codigoOrigem, String tokenId);

    Origens cadastrarOrigem(OrigemInput origemInput, String tokenId, HttpServletResponse response);

    Origens atualizarOrigem(String codigoOrigem, OrigemUpdateInput origemInput, String tokenId);

    void removerOrigem(String codigoOrigem);

    void atualizaStatusAtivo(String codigoOrigem, Boolean ativo);

    Origens findByCodigoOrigem(String codigoOrigem);

    List<Origens> buscaOrigemPorNome(String nomeOrigem, String tokenid);

    List<Origens> findByOrigemAtivo(String tokenId);
}
