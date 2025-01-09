package com.carrafasoft.bsuldo.api.mapper;

import com.carrafasoft.bsuldo.api.mapper.financeirodto.BancoRequestInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.BancoResponseRepresentation;
import com.carrafasoft.bsuldo.api.model.Bancos;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BancoMapper {

    public BancoResponseRepresentation toBancoResponseRepresentationMapper(Bancos bancos) {

        return BancoResponseRepresentation.builder()
                .codigoBanco(bancos.getCodigoBanco())
                .nomeBanco(bancos.getNomeBanco())
                .status(bancos.getStatus())
                .build();
    }

    public List<BancoResponseRepresentation> toListBancoResponseRepresentationMapper(List<Bancos> bancosList) {

        return bancosList.stream()
                .map(bancos -> toBancoResponseRepresentationMapper(bancos))
                .collect(Collectors.toList());
    }

    public Bancos toBancoMapper(BancoRequestInputRepresentation inputRepresentation, Long pessoaId) {

        return Bancos.builder()
                .nomeBanco(inputRepresentation.getNomeBanco())
                .status(inputRepresentation.getStatus())
                .pessoa(pessoaIdMapper(pessoaId))
                .build();
    }

    public Pessoas pessoaIdMapper(Long pessoaId) {

        Pessoas pessoaRetorno = new Pessoas();
        pessoaRetorno.setPessoaID(pessoaId);

        return pessoaRetorno;
    }
}
