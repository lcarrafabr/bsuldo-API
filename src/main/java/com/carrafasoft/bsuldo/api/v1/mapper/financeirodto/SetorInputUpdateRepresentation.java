package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetorInputUpdateRepresentation {

    @NotBlank
    private String codigoSetor;

    @NotBlank
    private String nomeSetor;

    private Boolean status;
}
