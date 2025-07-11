package com.carrafasoft.bsuldo.api.v1.v1.service;

import com.carrafasoft.bsuldo.api.v1.v1.modelo.PrecoCriptoBinance;


public interface BinanceApiService {

    PrecoCriptoBinance getPriceBiTicker(String ticker);

    PrecoCriptoBinance getCotacaoDollar();
}
