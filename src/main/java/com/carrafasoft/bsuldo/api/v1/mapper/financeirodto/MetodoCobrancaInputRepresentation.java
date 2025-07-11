package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class MetodoCobrancaInputRepresentation {

    @NotBlank
    private String nomeMetodoCob;
    private String descricao;


}
