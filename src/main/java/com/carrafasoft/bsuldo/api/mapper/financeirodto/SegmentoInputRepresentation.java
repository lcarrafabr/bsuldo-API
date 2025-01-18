package com.carrafasoft.bsuldo.api.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class SegmentoInputRepresentation {

    @NotBlank
    private String nomeSegmento;

    private Boolean status;
}
