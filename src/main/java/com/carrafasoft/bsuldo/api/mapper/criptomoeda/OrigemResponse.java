package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrigemResponse {

    private String codigoOrigem;
    private String nomeOrigem;
    private Boolean status;
}
