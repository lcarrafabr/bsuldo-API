package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.model.reports.criptos.CryptoGradeSaldoVariacao;

import java.util.List;

public interface CriptoReportsService {

    List<CryptoGradeSaldoVariacao> gerarReportGradeSaldoVariacao(String tokenId);
}
