package com.carrafasoft.bsuldo.api.service.serviceImpl.critomoeda;

import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletSaldoResponse;
import com.carrafasoft.bsuldo.api.model.criptomoedas.CriptoTransacao;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.CriptoTransacaoRepository;
import com.carrafasoft.bsuldo.api.service.CriptoTransacaoService;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CriptoTransacaoServiceImpl implements CriptoTransacaoService {

    @Autowired
    private CriptoTransacaoRepository repository;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<CriptoTransacao> listar() {

        List<CriptoTransacao> criptoTransacaoList = repository.findAll();
        List<WalletSaldoResponse> saldoWalletList = getSaldoWalletsList();

        // Criar um mapa para acesso rápido ao saldo por código da wallet
        Map<String, BigDecimal> saldoMap = saldoWalletList.stream()
                .collect(Collectors.toMap(WalletSaldoResponse::getCodigoWallet, WalletSaldoResponse::getSaldo));

// Atualizar o saldo dentro da lista criptoTransacaoList
        for (CriptoTransacao transacao : criptoTransacaoList) {
            Wallets wallet = transacao.getWallet();
            if (wallet != null) {
                BigDecimal saldo = saldoMap.getOrDefault(wallet.getCodigoWallet(), BigDecimal.ZERO);
                wallet.setSaldo(saldo); // Supondo que Wallet tenha um setSaldo()
            }
        }


        return criptoTransacaoList;
    }



    public List<WalletSaldoResponse> getSaldoWalletsList() {

        String sql = "SELECT  " +
                "w.codigo_wallet AS codigoWallet, " +
                "COALESCE(SUM( " +
                "CASE " +
                "WHEN ct.tipo_ordem_cripto IN ('COMPRA', 'BONIFICACAO') THEN ct.quantidade " +
                "WHEN ct.tipo_ordem_cripto IN ('VENDA', 'TRANSFERENCIA') THEN -ct.quantidade " +
                "ELSE 0 " +
                "END " +
                "), 0) AS saldo " +
                "FROM wallets w " +
                "LEFT JOIN cripto_transacao ct ON w.wallet_id = ct.wallet_id " +
                "GROUP BY w.wallet_id ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(WalletSaldoResponse.class));

        // Executar a consulta
        List<WalletSaldoResponse> resultados = query.getResultList();

        return resultados;
    }
}
