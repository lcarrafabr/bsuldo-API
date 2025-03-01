package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class OrigemUpdateInput {

    private String codigoOrigem;

    @NotBlank
    private String nomeOrigem;
    private Boolean status;
}
