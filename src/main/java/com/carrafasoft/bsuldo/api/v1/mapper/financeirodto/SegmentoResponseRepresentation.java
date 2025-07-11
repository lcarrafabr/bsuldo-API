package com.carrafasoft.bsuldo.api.v1.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SegmentoResponseRepresentation {

    private String codigoSegmento;
    private String nomeSegmento;
    private Boolean status;
}
