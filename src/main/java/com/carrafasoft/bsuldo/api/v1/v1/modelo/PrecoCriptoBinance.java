package com.carrafasoft.bsuldo.api.v1.v1.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PrecoCriptoBinance {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("price")
    private BigDecimal price;
}
