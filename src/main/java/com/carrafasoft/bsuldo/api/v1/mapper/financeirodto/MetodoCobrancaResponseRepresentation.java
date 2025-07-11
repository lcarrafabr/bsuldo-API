package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetodoCobrancaResponseRepresentation {

    private String codigoMetodoCobranca;
    private String nomeMetodoCob;
    private Boolean status;
    private String descricao;
}
