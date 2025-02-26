package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrigemResponse {

    private String codigoOrigem;
    private String nomeOrigem;
    private LocalDateTime dataUltimaAtualizacao;
}
