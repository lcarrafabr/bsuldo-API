package com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class OrigemResponse {

    private String codigoOrigem;
    private String nomeOrigem;
    private Boolean status;
}
