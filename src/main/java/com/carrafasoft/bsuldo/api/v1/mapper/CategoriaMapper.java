package com.carrafasoft.bsuldo.api.v1.mapper;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.CategoriaInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.CategoriaRequestRepesentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.CategoriaResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.Categorias;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoriaMapper {

    public CategoriaResponseRepresentation categoriaResponseRepresentationMapper(Categorias categorias) {

        return CategoriaResponseRepresentation.builder()
                .codigo(categorias.getCodigoCategoria())
                .nomeCategoria(categorias.getNomeCategoria())
                .status(categorias.getStatus())
                .descricao(categorias.getDescricao())
                .build();
    }

    public List<CategoriaResponseRepresentation> categoriaResponseRepresentationMapperList(List<Categorias> categorias) {

       return categorias.stream()
               .map(this::categoriaResponseRepresentationMapper)
               .collect(Collectors.toList());

    }

    public Categorias categoriaRequestRepesentationMapper(CategoriaRequestRepesentation requestRepesentation) {

        return Categorias.builder()
                .codigoCategoria(requestRepesentation.getCodigo())
                .nomeCategoria(requestRepesentation.getNomeCategoria())
                .status(requestRepesentation.getStatus())
                .descricao(requestRepesentation.getDescricao())
                .build();
    }

    public Categorias categoriaInputRepresentationMapper(CategoriaInputRepresentation inputRepresentation) {

        return Categorias.builder()
                .nomeCategoria(inputRepresentation.getNomeCategoria())
                .descricao(inputRepresentation.getDescricao())
                .build();
    }
}
