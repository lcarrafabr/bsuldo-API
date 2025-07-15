package com.carrafasoft.bsuldo.braviapi.modelo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrecoAtualCota {

    private String ticker;
    private BigDecimal valorAtualCota;
}
