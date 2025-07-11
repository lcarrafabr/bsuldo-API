package com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SetoresMapper {

    public SetorResponseRepresentation toSetorResponseRepresentationMapper(Setores setores) {

        return SetorResponseRepresentation.builder()
                .codigoSetor(setores.getCodigoSetor())
                .nomeSetor(setores.getNomeSetor())
                .status(setores.getStatus())
                .build();
    }

    public List<SetorResponseRepresentation> toListSetorRepresentationMapper(List<Setores> setoresList) {

        return setoresList.stream()
                .map(this::toSetorResponseRepresentationMapper)
                .collect(Collectors.toList());
    }

    public Setores toentity(SetorInputRepresentation setorInput, Pessoas pessoas) {

        return Setores.builder()
                .nomeSetor(setorInput.getNomeSetor())
                .pessoa(pessoas)
                .build();
    }
}
