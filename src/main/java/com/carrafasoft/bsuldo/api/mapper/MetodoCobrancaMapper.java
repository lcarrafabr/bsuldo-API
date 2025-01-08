package com.carrafasoft.bsuldo.api.mapper;

import com.carrafasoft.bsuldo.api.mapper.financeirodto.MetodoCobrancaInputRepresentation;
import com.carrafasoft.bsuldo.api.mapper.financeirodto.MetodoCobrancaResponseRepresentation;
import com.carrafasoft.bsuldo.api.model.MetodoDeCobranca;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MetodoCobrancaMapper {

    public MetodoCobrancaResponseRepresentation toMetodoCobrancaResponseRepresentationMapper(MetodoDeCobranca metodoDeCobranca) {

        return MetodoCobrancaResponseRepresentation.builder()
                .codigoMetodoCobranca(metodoDeCobranca.getCodigoMetodoCobranca())
                .nomeMetodoCob(metodoDeCobranca.getNomeMetodoCob())
                .status(metodoDeCobranca.getStatus())
                .descricao(metodoDeCobranca.getDescricao())
                .build();
    }

    public List<MetodoCobrancaResponseRepresentation> toListMetodoCobrancaResponseRepresentationMapper(List<MetodoDeCobranca> metodoDeCobrancaList) {

        return metodoDeCobrancaList.stream()
                .map(metodoCobranca -> toMetodoCobrancaResponseRepresentationMapper(metodoCobranca))
                .collect(Collectors.toList());
    }

    public MetodoDeCobranca toMetodoCobrancaMapper(MetodoCobrancaInputRepresentation inputRepresentation, Long pessoaId) {

        return MetodoDeCobranca.builder()
                .nomeMetodoCob(inputRepresentation.getNomeMetodoCob())
                .descricao(inputRepresentation.getDescricao())
                .pessoa(pessoaIdRepresentation(pessoaId))
                .build();
    }


    private Pessoas pessoaIdRepresentation(Long pessoaId) {

        Pessoas pessoaRetorno = new Pessoas();
        pessoaRetorno.setPessoaID(pessoaId);

        return pessoaRetorno;
    }
}
