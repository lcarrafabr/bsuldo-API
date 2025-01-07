package com.carrafasoft.bsuldo.api.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaRequestRepesentation  {

    private String codigo;
    private String nomeCategoria;
    private Boolean status;
    private String descricao;
}
