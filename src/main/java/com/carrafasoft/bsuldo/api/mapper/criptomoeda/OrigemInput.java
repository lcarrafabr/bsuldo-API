package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor  // Adicione esta anotação
@AllArgsConstructor // Garante que o @Builder continue funcionando
public class OrigemInput {

    @NotBlank
    private String nomeOrigem;
}
