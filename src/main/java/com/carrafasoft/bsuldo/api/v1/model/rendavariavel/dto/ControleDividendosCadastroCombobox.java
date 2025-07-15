package com.carrafasoft.bsuldo.api.v1.model.rendavariavel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControleDividendosCadastroCombobox {

    private String codigoProdutoRV;
    private String tipoProdutoEnum;
    private String ticker;


}
