package com.carrafasoft.bsuldo.api.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class SegmentoInputUpdateRepresentation {

    @NotBlank
    private String codigoSegmento;

    @NotBlank
    private String nomeSegmento;

    private Boolean status;
}
