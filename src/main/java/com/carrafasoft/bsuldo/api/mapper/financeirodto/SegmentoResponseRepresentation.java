package com.carrafasoft.bsuldo.api.mapper.financeirodto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SegmentoResponseRepresentation {

    private String codigoSegmento;
    private String nomeSegmento;
    private Boolean status;
}
