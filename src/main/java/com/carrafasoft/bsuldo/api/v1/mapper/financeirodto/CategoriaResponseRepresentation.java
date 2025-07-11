package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponseRepresentation {


    private String codigo;
    private String nomeCategoria;
    private Boolean status;
    private String descricao;
}
