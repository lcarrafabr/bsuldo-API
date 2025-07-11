package com.carrafasoft.bsuldo.api.v1.service.validations;

import com.carrafasoft.bsuldo.api.v1.enums.TipoOrdemCriptoEnum;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.CriptoTransacaoInput;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CriptoTransacaoValidator {

    private final String ORDEM_COMPRA_VALOR_INVESTIDO_IGUAL_ZERO = "O tipo de ordem 'COMPRA' não deve ter valor investido zero";
    private final String ORDEM_COMPRA_QUANTIDADE_IGUAL_ZERO = "O tipo de ordem 'COMPRA' não deve ter quantidade (0) zero.";
    private final String ORDEM_VENDA_QUANTIDADE_IGUAL_ZERO = "O tipo de ordem 'VENDA' não deve ter quantidade (0) zero.";
    private final String ORDEM_VENDA_VALOR_INVESTIDO_IGUAL_ZERO = "O tipo de ordem 'VENDA' não deve ter valor investido (0) zero.";
    private final String TRANSFERENCIA_SAIDA_NAO_DEVE_TER_QUANTIDADE_ZERO = "Transferência de saída e queima não devem ter quantidade (0) zero.";
    private final String RECOMPENSA_E_RECEBIMENTO_DEVEM_TER_QUANTIDADE_POSITIVA = "Recompensas e recebimentos devem ter quantidade positiva.";
    private final String MINERACAO_AIRDROP_STAKING_NAO_DEVE_TER_VALOR_INVESTIDO = "AIRDROP, STAKING e MINERAÇÃO não devem ter valor investido.";

    public void validar(CriptoTransacaoInput input) {
        if (input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.COMPRA) {
            if (input.getValorInvestido().compareTo(BigDecimal.ZERO) == 0) {
                throw new NegocioException(ORDEM_COMPRA_VALOR_INVESTIDO_IGUAL_ZERO);
            }

            if (input.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
                throw new NegocioException(ORDEM_COMPRA_QUANTIDADE_IGUAL_ZERO);
            }
        }

        if (input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.VENDA) {
            if (input.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
                throw new NegocioException(ORDEM_VENDA_QUANTIDADE_IGUAL_ZERO);
            }
        }

        if (input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.VENDA) {
            if (input.getValorInvestido().compareTo(BigDecimal.ZERO) == 0) {
                throw new NegocioException(ORDEM_VENDA_VALOR_INVESTIDO_IGUAL_ZERO);
            }
        }

        if (input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.TRANSFERENCIA_SAIDA
                || input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.QUEIMA) {

            if (input.getQuantidade().compareTo(BigDecimal.ZERO) == 0) {
                throw new NegocioException(TRANSFERENCIA_SAIDA_NAO_DEVE_TER_QUANTIDADE_ZERO);
            }
        }

        if (input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.AIRDROP
                || input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.STAKING_RECOMPENSA
                || input.getTipoOrdemCripto() == TipoOrdemCriptoEnum.MINERACAO) {
            if (input.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
                throw new NegocioException(RECOMPENSA_E_RECEBIMENTO_DEVEM_TER_QUANTIDADE_POSITIVA);
            }
        }

        if ((input.getTipoOrdemCripto().equals(TipoOrdemCriptoEnum.AIRDROP) ||
                input.getTipoOrdemCripto().equals(TipoOrdemCriptoEnum.STAKING_RECOMPENSA) ||
                input.getTipoOrdemCripto().equals(TipoOrdemCriptoEnum.MINERACAO))
                && input.getValorInvestido().compareTo(BigDecimal.ZERO) != 0) {
            throw new NegocioException(MINERACAO_AIRDROP_STAKING_NAO_DEVE_TER_VALOR_INVESTIDO);
        }

    }
}
