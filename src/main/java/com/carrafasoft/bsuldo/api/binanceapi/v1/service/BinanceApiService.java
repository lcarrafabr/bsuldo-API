package com.carrafasoft.bsuldo.api.binanceapi.v1.service;

import com.carrafasoft.bsuldo.api.binanceapi.v1.modelo.PrecoCriptoBinance;


public interface BinanceApiService {

    PrecoCriptoBinance getPriceBiTicker(String ticker);

    PrecoCriptoBinance getCotacaoDollar();
}
