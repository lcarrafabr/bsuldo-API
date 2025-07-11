package com.carrafasoft.bsuldo.api.v1.model.reports;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TotalMetodoCobrancaMes {

	private String nomeMetodoCobranca;
	private BigDecimal totais;

}
