package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorInputUpdateRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;

import javax.servlet.http.HttpServletResponse;

public interface SetoresService {


    SetorResponseRepresentation cadastrarSetor(SetorInputRepresentation setorInput, HttpServletResponse response, String tokenId);
    Setores findByCodigoSetorAndTokenId(String codigoSetor, String tokenId);

    SetorResponseRepresentation atualizarSetor(String codigo, SetorInputUpdateRepresentation setor, String tokenId);

    void removerSetor(String codigo);

    void atualizaStatusAtivo(String codigo, Boolean ativo);


}
