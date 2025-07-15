package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import com.carrafasoft.bsuldo.api.v1.model.Emissores;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoRvInputRepresentation {

    private String codigoProdutoRV;
    private String longName;
    private String shortName;

    @NotBlank
    private String ticker;

    private String currency;
    private String cnpj;
    private Boolean geraDividendos;
    private Boolean status;
    private Long cotasEmitidas;
    private String logoUrl;
    private String descricao;
    private Emissores emissor; //TODO criar representation de EMISSORES
    private SegmentoResponseRepresentation segmento;
    private SetorResponseRepresentation setor;
}
