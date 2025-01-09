package com.carrafasoft.bsuldo.api.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class BancoResponseRepresentation {

    private String codigoBanco;

    @NotBlank
    private String nomeBanco;
    private Boolean status;
}
