package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class MetodoCobrancaRequestInputRepresentation {

    @NotBlank
    private String codigoMetodoCobranca;
    @NotBlank
    private String nomeMetodoCob;
    private Boolean status;
    private String descricao;
}
