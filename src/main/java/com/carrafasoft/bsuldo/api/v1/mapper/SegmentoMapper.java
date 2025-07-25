package com.carrafasoft.bsuldo.api.v1.mapper;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SegmentoMapper {

    public SegmentoResponseRepresentation toSegmentoResponseRepresentationMapper(Segmentos segmentos) {

        return SegmentoResponseRepresentation.builder()
                .codigoSegmento(segmentos.getCodigoSegmento())
                .nomeSegmento(segmentos.getNomeSegmento())
                .status(segmentos.getStatus())
                .build();
    }

    public List<SegmentoResponseRepresentation> toListSegmentoResponseRepresentationMapper(List<Segmentos> segmentosList) {

        return segmentosList.stream()
                .map(this::toSegmentoResponseRepresentationMapper)
                .collect(Collectors.toList());
    }

    public Segmentos toEntity(SegmentoInputRepresentation segmentpInput, Pessoas pessoas) {

        return Segmentos.builder()
                .nomeSegmento(segmentpInput.getNomeSegmento())
                .status(segmentpInput.getStatus())
                .pessoa(pessoas)
                .build();
    }
}
