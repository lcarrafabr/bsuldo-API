package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetorInputRepresentation {

    @NotBlank
    private String nomeSetor;
}
