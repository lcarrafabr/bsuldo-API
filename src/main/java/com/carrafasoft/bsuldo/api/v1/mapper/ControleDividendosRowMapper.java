package com.carrafasoft.bsuldo.api.v1.mapper;

import com.carrafasoft.bsuldo.api.v1.enums.TipoAtivoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDivRecebimentoEnum;
import com.carrafasoft.bsuldo.api.v1.enums.TipoDividendoEnum;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Segmentos;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ControleDividendosRowMapper implements RowMapper<ControleDividendos> {

    @Override
    public ControleDividendos mapRow(ResultSet rs, int rowNum) throws SQLException {
        ControleDividendos controleDividendos = new ControleDividendos();

        controleDividendos.setControleDividendoId(rs.getLong("controle_dividendos_id"));
        controleDividendos.setCodigoControleDividendo(rs.getString("codigo_controle_dividendo"));
        controleDividendos.setTipoAtivoEnum(TipoAtivoEnum.valueOf(rs.getString("tipo_ativo_enum")));
        controleDividendos.setTipoDivRecebimentoEnum(TipoDivRecebimentoEnum.valueOf(rs.getString("tipo_div_recebimento_enum")));
        controleDividendos.setDataReferencia(rs.getDate("data_referencia").toLocalDate());
        controleDividendos.setTipoDividendoEnum(TipoDividendoEnum.valueOf(rs.getString("tipo_dividendo_enum")));
        controleDividendos.setDataCom(rs.getDate("data_com").toLocalDate());
        controleDividendos.setDataPagamento(rs.getDate("data_pagamento").toLocalDate());
        controleDividendos.setValorPorCota(rs.getBigDecimal("valor_por_cota"));
        controleDividendos.setValorRecebido(rs.getBigDecimal("valor_recebido"));
        controleDividendos.setDivUtilizado(rs.getBoolean("div_utilizado"));
        controleDividendos.setQtdCota(rs.getInt("qtdCota"));

        // Mapear campos de Pessoas
        Pessoas pessoa = new Pessoas();
        pessoa.setPessoaID(rs.getLong("pessoa_id"));
        pessoa.setNomePessoa(rs.getString("nome_pessoa"));
        controleDividendos.setPessoa(pessoa);

        // Mapear campos de ProdutosRendaVariavel
        ProdutosRendaVariavel produto = new ProdutosRendaVariavel();
        produto.setProdutoId(rs.getLong("pr.produto_id")); // Certifique-se de que o alias está correto
        produto.setCodigoProdutoRV(rs.getString("codigo_produto_rv"));
        produto.setLongName(rs.getString("pr.long_name"));
        produto.setShortName(rs.getString("pr.short_name"));
        produto.setTicker(rs.getString("pr.ticker"));
        produto.setCurrency(rs.getString("pr.currency"));
        produto.setCnpj(rs.getString("pr.cnpj"));
        produto.setGeraDividendos(rs.getBoolean("pr.gera_dividendos"));
        //produto.setStatus(rs.getBoolean("pr.produto_status")); // Alias deve corresponder
        produto.setCotasEmitidas(rs.getLong("pr.cotas_emitidas"));
        produto.setLogoUrl(rs.getString("pr.logo_url"));
        produto.setDescricao(rs.getString("pr.descricao"));


        // Mapear campos de Segmentos
        Segmentos segmento = new Segmentos();
        segmento.setSegmentoId(rs.getLong("segmento_id"));
        segmento.setCodigoSegmento(rs.getString("codigo_segmento"));
        segmento.setNomeSegmento(rs.getString("nome_segmento"));
        //segmento.setStatus(rs.getBoolean("status"));
        produto.setSegmento(segmento);

        // Mapear campos de Setores
        Setores setor = new Setores();
        setor.setSetorId(rs.getLong("setor_id"));
        setor.setCodigoSetor(rs.getString("codigo_setor"));
        setor.setNomeSetor(rs.getString("nome_setor"));
        //setor.setStatus(rs.getBoolean("status"));
        produto.setSetor(setor);

        controleDividendos.setProdutosRendaVariavel(produto);

        return controleDividendos;
    }
}
