package com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraInputReppresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.OrdemDeCompraResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.OrdensDeCompra;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdemDeCompraMapper {

    private final ProdutoRendaVariavelMapper produtoRendaVariavelMapper;

    public OrdemDeCompraMapper(ProdutoRendaVariavelMapper produtoRendaVariavelMapper) {
        this.produtoRendaVariavelMapper = produtoRendaVariavelMapper;
    }

    public OrdemDeCompraResponseRepresentation toOrdemDeCompraResponseRepresentation(OrdensDeCompra ordensDeCompra) {

        return OrdemDeCompraResponseRepresentation.builder()
                .codigoOrdemDeComppra(ordensDeCompra.getCodigoOrdemDeComppra())
                .tipoAtivoEnum(ordensDeCompra.getTipoAtivoEnum())
                .tipoOrdemRendaVariavelEnum(ordensDeCompra.getTipoOrdemRendaVariavelEnum())
                .dataTransacao(ordensDeCompra.getDataTransacao())
                .dataExecucao(ordensDeCompra.getDataExecucao())
                .quantidadeCotas(ordensDeCompra.getQuantidadeCotas())
                .precoUnitarioCota(ordensDeCompra.getPrecoUnitarioCota())
                .valorInvestido(ordensDeCompra.getValorInvestido())
                .desdobroAgrupado(ordensDeCompra.getDesdobroAgrupado())
                .dataDesdobroAgrupamento(ordensDeCompra.getDataDesdobroAgrupamento())
                .tipoOrdemManualAutoEnum(ordensDeCompra.getTipoOrdemManualAutoEnum())

                .produtoRendaVariavel(produtoRendaVariavelMapper.toProdutoRVResponseRepresentation(
                        ordensDeCompra.getProdutoRendaVariavel()
                ))
                .build();
    }

    public List<OrdemDeCompraResponseRepresentation> toListOrdemDeCompraRepresentation(List<OrdensDeCompra> ordensDeCompraList) {

        return ordensDeCompraList.stream()
                .map(this::toOrdemDeCompraResponseRepresentation)
                .collect(Collectors.toList());
    }

    public OrdensDeCompra toEntity(OrdemDeCompraInputReppresentation ordemCompra, Pessoas pessoaSalva,
                                   ProdutosRendaVariavel produtosRendaVariavelSalvo) {

        return OrdensDeCompra.builder()
                .tipoAtivoEnum(ordemCompra.getTipoAtivoEnum())
                .tipoOrdemRendaVariavelEnum(ordemCompra.getTipoOrdemRendaVariavelEnum())
                .dataTransacao(ordemCompra.getDataTransacao())
                .dataExecucao(ordemCompra.getDataExecucao())
                .quantidadeCotas(ordemCompra.getQuantidadeCotas())
                .precoUnitarioCota(ordemCompra.getPrecoUnitarioCota())
                .valorInvestido(ordemCompra.getValorInvestido())
                .desdobroAgrupado(ordemCompra.getDesdobroAgrupado())
                .dataDesdobroAgrupamento(ordemCompra.getDataDesdobroAgrupamento())
                .tipoOrdemManualAutoEnum(ordemCompra.getTipoOrdemManualAutoEnum())
                .pessoa(pessoaSalva)
                .produtoRendaVariavel(produtosRendaVariavelSalvo)
                .build();
    }
}
