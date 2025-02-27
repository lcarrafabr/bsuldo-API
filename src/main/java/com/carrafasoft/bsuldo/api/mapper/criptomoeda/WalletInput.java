package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import com.carrafasoft.bsuldo.api.enums.MoedaEnum;
import com.carrafasoft.bsuldo.api.enums.TipoCarteiraEnum;
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
