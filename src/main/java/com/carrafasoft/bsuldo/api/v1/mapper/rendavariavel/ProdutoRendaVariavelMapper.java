package com.carrafasoft.bsuldo.api.v1.mapper.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRVResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.ProdutoRvInputRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SegmentoResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.mapper.financeirodto.SetorResponseRepresentation;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProdutoRendaVariavelMapper {

    public ProdutoRVResponseRepresentation toProdutoRVResponseRepresentation(ProdutosRendaVariavel produtoRV) {

        return ProdutoRVResponseRepresentation.builder()
                .codigoProdutoRV(produtoRV.getCodigoProdutoRV())
                .longName(produtoRV.getLongName())
                .shortName(produtoRV.getShortName())
                .ticker(produtoRV.getTicker())
                .currency(produtoRV.getCurrency())
                .cnpj(produtoRV.getCnpj())
                .geraDividendos(produtoRV.getGeraDividendos())
                .status(produtoRV.getStatus())
                .cotasEmitidas(produtoRV.getCotasEmitidas())
                .logoUrl(produtoRV.getLogoUrl())
                .descricao(produtoRV.getDescricao())
                .emissor(produtoRV.getEmissor())
                .segmento(getSegmentoRepresentation(produtoRV.getSegmento()))
                .setor(getSetorRepresentation(produtoRV.getSetor()))
                .build();
    }

    public List<ProdutoRVResponseRepresentation> toListProdutoRendaVariavelRepresentation(List<ProdutosRendaVariavel> produtosRendaVariavelList) {

        return produtosRendaVariavelList.stream()
                .map(this::toProdutoRVResponseRepresentation)
                .collect(Collectors.toList());
    }

    private SetorResponseRepresentation getSetorRepresentation(Setores setor) {

        return SetorResponseRepresentation.builder()
                .codigoSetor(setor.getCodigoSetor())
                .nomeSetor(setor.getNomeSetor())
                .status(setor.getStatus())
                .build();
    }

    private SegmentoResponseRepresentation getSegmentoRepresentation(Segmentos segmento) {

        return SegmentoResponseRepresentation.builder()
                .codigoSegmento(segmento.getCodigoSegmento())
                .nomeSegmento(segmento.getNomeSegmento())
                .status(segmento.getStatus())
                .build();
    }

    public ProdutosRendaVariavel toEntity(ProdutoRvInputRepresentation produtosRendaVariavel, Pessoas pessoaSalva, Segmentos segmentoSalvo, Setores setorSalvo) {


        return ProdutosRendaVariavel.builder()
                .longName(produtosRendaVariavel.getLongName())
                .shortName(produtosRendaVariavel.getShortName())
                .ticker(produtosRendaVariavel.getTicker())
                .currency("BRL")
                .cnpj(produtosRendaVariavel.getCnpj())
                .geraDividendos(produtosRendaVariavel.getGeraDividendos())
                .status(produtosRendaVariavel.getStatus())
                .cotasEmitidas(produtosRendaVariavel.getCotasEmitidas())
                .logoUrl(produtosRendaVariavel.getLogoUrl())
                .descricao(produtosRendaVariavel.getDescricao())
                .emissor(produtosRendaVariavel.getEmissor()) //TODO ajustar EMISSOR
                .segmento(segmentoSalvo)
                .setor(setorSalvo)
                .pessoa(pessoaSalva)
                .build();
    }
}
