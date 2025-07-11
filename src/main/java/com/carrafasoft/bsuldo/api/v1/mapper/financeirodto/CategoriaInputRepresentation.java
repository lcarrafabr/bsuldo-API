package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaInputRepresentation {

    private String nomeCategoria;
    private String descricao;
}
