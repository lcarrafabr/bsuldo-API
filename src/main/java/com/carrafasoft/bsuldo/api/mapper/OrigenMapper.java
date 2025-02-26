package com.carrafasoft.bsuldo.api.mapper;

import com.carrafasoft.bsuldo.api.mapper.criptomoeda.OrigemResponse;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrigenMapper {

    public OrigemResponse toOriremResponse(Origens origen) {

        return OrigemResponse.builder()
                .codigoOrigem(origen.getCodigoOrigem())
                .nomeOrigem(origen.getNomeOrigem())
                .dataUltimaAtualizacao(origen.getDataUltimaAtualizacao())
                .build();
    }

    public List<OrigemResponse> toListOrigemResponse(List<Origens> origemList) {

        return origemList.stream()
                .map(this::toOriremResponse)
                .collect(Collectors.toList());
    }
}
