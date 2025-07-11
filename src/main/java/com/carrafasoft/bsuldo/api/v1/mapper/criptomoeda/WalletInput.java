package com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda;

import com.carrafasoft.bsuldo.api.v1.enums.TipoCarteiraEnum;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class WalletInput {

    @NotBlank
    private String nomeCarteira;

    @NotNull
    private TipoCarteiraEnum tipoCarteira;

    @NotNull
    private OrigemResponse origem;
}
