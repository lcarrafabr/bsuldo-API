package com.carrafasoft.bsuldo.api.v1.service.serviceImpl.critomoeda;

import com.carrafasoft.bsuldo.api.v1.v1.modelo.PrecoCriptoBinance;
import com.carrafasoft.bsuldo.api.v1.v1.service.BinanceApiService;
import com.carrafasoft.bsuldo.api.v1.model.reports.criptos.CryptoGradeSaldoVariacao;
import com.carrafasoft.bsuldo.api.v1.service.CriptoReportsService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CriptoReportsServiceImpl implements CriptoReportsService {


    private final PessoaService pessoaService;

    @PersistenceContext
    private EntityManager entityManager;

    private final BinanceApiService binanceApiService;

    public CriptoReportsServiceImpl(PessoaService pessoaService,
                                    BinanceApiService binanceApiService) {
        this.pessoaService = pessoaService;
        this.binanceApiService = binanceApiService;
    }

    @Override
    public List<CryptoGradeSaldoVariacao> gerarReportGradeSaldoVariacao(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);


        String sql = "WITH TotalCarteira AS ( " + //esta é a CTE
                "SELECT " +
                "SUM(ABS(valor_investido)) AS total_investido_geral " +
                "FROM cripto_transacao " +
                "where pessoa_id = :pessoaId " +
                ") " +

                "SELECT " +
                "ct.moeda, " +
                "SUM(ct.quantidade) AS quantidadeTotal, " +
                "SUM(ABS(ct.valor_investido)) AS valorInvestidoTotal, " +
                "AVG(ABS(ct.valor_investido)) AS mediaInvestida, " +
                "(SUM(ABS(ct.valor_investido)) / tc.total_investido_geral) * 100 AS percentualCarteira, " +
                "SUM(ABS(ct.valor_investido)) / SUM(ct.quantidade) AS precoMedio " +
                "FROM cripto_transacao ct " +
                "JOIN TotalCarteira tc " +
                "ON 1=1 " + //Apenas para referenciar a CTE
                "WHERE ct.pessoa_id = :pessoaId " +
                "GROUP BY ct.moeda " +
                "ORDER BY percentualCarteira DESC ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        query.setParameter("pessoaId", pessoaId);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(CryptoGradeSaldoVariacao.class));

        // Executar a consulta
        List<CryptoGradeSaldoVariacao> resultados = query.getResultList();

        calculaVariacoes(resultados);

        return resultados;
    }


    private void calculaVariacoes(List<CryptoGradeSaldoVariacao> cryptoGradeSaldoVariacaoList) {

        for (CryptoGradeSaldoVariacao item : cryptoGradeSaldoVariacaoList) {
            String moeda = item.getMoeda();
            PrecoCriptoBinance cotacaoAtual = binanceApiService.getPriceBiTicker(moeda);

            if (cotacaoAtual != null && cotacaoAtual.getPrice() != null) {
                // Atualiza a cotação da criptomoeda
                item.setCotacaoAtual(cotacaoAtual.getPrice());

                // Calcula o saldo atual: cotação atual * quantidade total
                BigDecimal saldoAtual = cotacaoAtual.getPrice().multiply(item.getQuantidadeTotal());
                item.setSaldoVariacao(saldoAtual);

                // Calcula o projetivo (lucro/prejuízo absoluto em reais)
                BigDecimal projetivo = saldoAtual.subtract(item.getValorInvestidoTotal());
                item.setProjetivo(projetivo);

                // Calcula a variação percentual em relação ao valor investido
                if (item.getValorInvestidoTotal().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal variacaoPercentual = projetivo
                            .divide(item.getValorInvestidoTotal(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    item.setVariacao(variacaoPercentual);
                } else {
                    item.setVariacao(null); // Evita divisão por zero
                }
            } else {
                // Se não encontrar a cotação, define como null
                item.setCotacaoAtual(null);
                item.setSaldoVariacao(null);
                item.setProjetivo(null);
                item.setVariacao(null);
            }
        }
    }


}

