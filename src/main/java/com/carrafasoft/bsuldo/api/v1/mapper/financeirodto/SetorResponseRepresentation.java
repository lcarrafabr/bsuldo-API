package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetorResponseRepresentation {

    private String codigoSetor;
    private String nomeSetor;
    private Boolean status;
}
