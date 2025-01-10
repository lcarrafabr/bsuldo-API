package com.carrafasoft.bsuldo.api.mapper;

import com.carrafasoft.bsuldo.api.mapper.financeirodto.*;
import com.carrafasoft.bsuldo.api.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LancamentoMapper {

    public LancamentoResponseRepresentation toLancamentoResponseRepresentationMapper(Lancamentos lancamentos) {

        return LancamentoResponseRepresentation.builder()
                .codigoLancamento(lancamentos.getCodigoLancamento())
                .valor(lancamentos.getValor())
                .dataVencimento(lancamentos.getDatavencimento())
                .dataPagamento(lancamentos.getDataPagamento())
                .descricao(lancamentos.getDescricao())
                .situacao(lancamentos.getSituacao())
                .parcelado(lancamentos.getParcelado())
                .quantidadeParcelas(lancamentos.getQuantidadeParcelas())
                .numeroParcela(lancamentos.getNumeroParcela())
                .chavePesquisa(lancamentos.getChavePesquisa())
                .lancRecorrente(lancamentos.getLancRecorrente())
                .tipoLancamento(lancamentos.getTipoLancamento())
                .categoria(categoriaResponseRepresentation(lancamentos))
                .metodoDeCobranca(metodoCobrancaRepresentation(lancamentos))
                .banco(bancoResponseRepresentation(lancamentos))
                .build();
    }

    public List<LancamentoResponseRepresentation> toListLancamentoResponseRepresentationMapper(List<Lancamentos> lancamentosList) {

        return lancamentosList.stream()
                .map(this::toLancamentoResponseRepresentationMapper)
                .collect(Collectors.toList());
    }

    public Lancamentos toLancamentoMapper(LancamentoInputRepresentation lancamentoInput, Categorias categorias,
                                          MetodoDeCobranca metodoDeCobranca, Bancos bancos, Pessoas pessoas) {

        return Lancamentos.builder()
                .valor(lancamentoInput.getValor())
                .datavencimento(lancamentoInput.getDataVencimento())
                .dataPagamento(lancamentoInput.getDataPagamento())
                .descricao(lancamentoInput.getDescricao())
                .situacao(lancamentoInput.getSituacao())
                .parcelado(lancamentoInput.getParcelado())
                .quantidadeParcelas(lancamentoInput.getQuantidadeParcelas())
                .numeroParcela(lancamentoInput.getNumeroParcela())
                .chavePesquisa(lancamentoInput.getChavePesquisa())
                .lancRecorrente(lancamentoInput.getLancRecorrente())
                .tipoLancamento(lancamentoInput.getTipoLancamento())
                .categoria(categorias)
                .metodoDeCobranca(metodoDeCobranca)
                .banco(bancos)
                .pessoa(pessoas)
                .build();
    }

    private CategoriaResponseRepresentation categoriaResponseRepresentation(Lancamentos lancamentos) {

        return CategoriaResponseRepresentation.builder()
                .codigo(lancamentos.getCategoria().getCodigoCategoria())
                .nomeCategoria(lancamentos.getCategoria().getNomeCategoria())
                .status(lancamentos.getCategoria().getStatus())
                .descricao(lancamentos.getCategoria().getDescricao())
                .build();
    }

    private MetodoCobrancaResponseRepresentation metodoCobrancaRepresentation(Lancamentos lancamentos) {

        return MetodoCobrancaResponseRepresentation.builder()
                .codigoMetodoCobranca(lancamentos.getMetodoDeCobranca().getCodigoMetodoCobranca())
                .nomeMetodoCob(lancamentos.getMetodoDeCobranca().getNomeMetodoCob())
                .status(lancamentos.getMetodoDeCobranca().getStatus())
                .descricao(lancamentos.getMetodoDeCobranca().getDescricao())
                .build();
    }

    private BancoResponseRepresentation bancoResponseRepresentation(Lancamentos lancamentos) {

        if (lancamentos.getBanco() == null) {
            return null;
        }

        return BancoResponseRepresentation.builder()
                .codigoBanco(lancamentos.getBanco().getCodigoBanco())
                .nomeBanco(lancamentos.getBanco().getNomeBanco())
                .status(lancamentos.getBanco().getStatus())
                .build();
    }

}
