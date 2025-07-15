package com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendoInput;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ControleDividendosResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRVResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ControleDividendosMapper {

    private final ProdutoRendaVariavelMapper produtoRendaVariavelMapper;

    public ControleDividendosMapper(ProdutoRendaVariavelMapper produtoRendaVariavelMapper) {
        this.produtoRendaVariavelMapper = produtoRendaVariavelMapper;
    }

    public ControleDividendosResponseRepresentation toControleDividendoRepresentation(ControleDividendos controleDividendos) {

        return ControleDividendosResponseRepresentation.builder()
                .codigoControleDividendo(controleDividendos.getCodigoControleDividendo())
                .tipoAtivoEnum(controleDividendos.getTipoAtivoEnum())
                .tipoDivRecebimentoEnum(controleDividendos.getTipoDivRecebimentoEnum())
                .dataReferencia(controleDividendos.getDataReferencia())
                .tipoDividendoEnum(controleDividendos.getTipoDividendoEnum())
                .dataCom(controleDividendos.getDataCom())
                .dataPagamento(controleDividendos.getDataPagamento())
                .valorPorCota(controleDividendos.getValorPorCota())
                .valorRecebido(controleDividendos.getValorRecebido())
                .divUtilizado(controleDividendos.getDivUtilizado())

                //É retornado a quantidade de ativos na grade Quantidade da tela de pesquisa controle dividendos
                .qtdCota(controleDividendos.getQtdCota())

                .produtosRendaVariavel(
                        produtoRendaVariavelMapper.toProdutoRVResponseRepresentation(controleDividendos.getProdutosRendaVariavel()))
                .build();
    }

    public List<ControleDividendosResponseRepresentation> toListControleDividendoRepresentation(List<ControleDividendos> controleDividendosList) {

        return controleDividendosList.stream()
                .map(this::toControleDividendoRepresentation)
                .collect(Collectors.toList());
    }

    public ControleDividendos toEntity(ControleDividendoInput controleDividendos, Pessoas pessoaSalva, ProdutosRendaVariavel produtosRendaVariavelSalvo) {

        return ControleDividendos.builder()
                .tipoAtivoEnum(controleDividendos.getTipoAtivoEnum())
                .tipoDivRecebimentoEnum(controleDividendos.getTipoDivRecebimentoEnum())
                .dataReferencia(controleDividendos.getDataReferencia())
                .tipoDividendoEnum(controleDividendos.getTipoDividendoEnum())
                .dataCom(controleDividendos.getDataCom())
                .dataPagamento(controleDividendos.getDataPagamento())
                .valorPorCota(controleDividendos.getValorPorCota())
                .valorRecebido(controleDividendos.getValorRecebido())
                .divUtilizado(controleDividendos.getDivUtilizado())
                .pessoa(pessoaSalva)
                .produtosRendaVariavel(produtosRendaVariavelSalvo)
                .build();
    }
}
