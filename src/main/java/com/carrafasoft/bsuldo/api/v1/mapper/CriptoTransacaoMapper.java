package com.carrafasoft.bsuldo.api.v1.mapper;

import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.CriptoTransacaoInput;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.CriptoTransacaoResponse;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.OrigemResponse;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.WalletResponse;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.CriptoTransacao;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.Origens;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.Wallets;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CriptoTransacaoMapper {

    public CriptoTransacaoResponse toCriptoTransacaoResponse(CriptoTransacao criptoTransacao) {

        return CriptoTransacaoResponse.builder()
                .codigoCriptoTransacao(criptoTransacao.getCodigoCrioptoTransacao())
                .moeda(criptoTransacao.getMoeda())
                .quantidade(criptoTransacao.getQuantidade())
                .precoNegociacao(criptoTransacao.getPrecoNegociacao())
                .valorInvestido(criptoTransacao.getValorInvestido())
                .tipoOrdemCripto(criptoTransacao.getTipoOrdemCripto())
                .dataTransacao(criptoTransacao.getDataTransacao())
                .wallet(getWalletRepresentation(criptoTransacao.getWallet()))
                .build();
    }

    public List<CriptoTransacaoResponse> toListCriptoTransacaoResponse(List<CriptoTransacao> criptoTransacaoList) {

        return criptoTransacaoList.stream()
                .map(this::toCriptoTransacaoResponse)
                .collect(Collectors.toList());
    }

    public CriptoTransacao toCriptoTransacaoModel(CriptoTransacaoInput transacaoInput, Pessoas pessoa, Wallets wallet) {

        return CriptoTransacao.builder()
                .moeda(transacaoInput.getMoeda())
                .quantidade(transacaoInput.getQuantidade())
                .precoNegociacao(transacaoInput.getPrecoNegociacao())
                .valorInvestido(transacaoInput.getValorInvestido())
                .tipoOrdemCripto(transacaoInput.getTipoOrdemCripto())
                .pessoa(pessoa)
                .wallet(wallet)
                .build();
    }

    private WalletResponse getWalletRepresentation(Wallets wallet) {

        return WalletResponse.builder()
                .codigoWallet(wallet.getCodigoWallet())
                .nomeCarteira(wallet.getNomeCarteira())
                .tipoCarteira(wallet.getTipoCarteira())
                .saldo(wallet.getSaldo())
                .status(wallet.getStatus())
                .dataCriacao(wallet.getDataCriacao())
                .dataUltimaAtualizacao(wallet.getDataUltimaAtualizacao())
                .origem(getOrigemRepresentation(wallet.getOrigem()))
                .build();
    }

    private OrigemResponse getOrigemRepresentation(Origens origem) {

        return OrigemResponse.builder()
                .codigoOrigem(origem.getCodigoOrigem())
                .nomeOrigem(origem.getNomeOrigem())
                .status(origem.getStatusAtivo())
                .build();
    }
}
