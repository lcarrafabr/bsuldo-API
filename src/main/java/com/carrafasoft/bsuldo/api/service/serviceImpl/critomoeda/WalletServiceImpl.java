package com.carrafasoft.bsuldo.api.service.serviceImpl.critomoeda;

import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.mapper.WalletMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletSaldoResponse;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.WalletNaoEncontradoException;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.WalletRepository;
import com.carrafasoft.bsuldo.api.service.OrigemService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository repository;

    @Autowired
    private WalletMapper mapper;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private OrigemService origemService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Wallets findById(String codigoWallet, String tokenId) {
        return repository.findByCodigoWalletAndPessoaId(codigoWallet, pessoaService.recuperaIdPessoaByToken(tokenId))
                .orElseThrow(() -> new WalletNaoEncontradoException(codigoWallet));
    }

    @Transactional
    @Override
    public Wallets cadastrarVallet(WalletInput walletInput, String tokenId, HttpServletResponse response) {

       try {
           log.info("...: Preparando cadastro de WALLET do usuario: {}", tokenId);

           Origens origemSalva = origemService.findByCodigoOrigem(walletInput.getOrigem().getCodigoOrigem());
           Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

           Wallets walletToSave = mapper.toWalletModel(walletInput, origemSalva, pessoaSalva);

           Wallets walletCadastrado = repository.save(walletToSave);

           publisher.publishEvent(new RecursoCriadoEvent(this, response, walletCadastrado.getWalletId()));

           log.info("...: Wallet Cadastrado com sucesso. :...");

           return walletCadastrado;
       } catch (EntidadeNaoEncontradaException e) {
           throw new NegocioException(e.getMessage());
       }
    }

    @Transactional
    @Override
    public Wallets atualizarWallet(String codigoWallet, WalletInput walletInput, String tokenId) {

        try {
            Wallets walletSalvo = findById(codigoWallet, tokenId);
            BeanUtils.copyProperties(walletInput, walletSalvo, "walletId");
            return repository.save(walletSalvo);

        }catch (WalletNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void removerWalet(String codigoWallet) {

        try {
            repository.deleteByCodigoWallet(codigoWallet);
        } catch (EntidadeNaoEncontradaException e) {
            throw new WalletNaoEncontradoException(codigoWallet);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeNaoEncontradaException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void atualizaStatusAtivo(String codigoWallet, Boolean ativo) {

        try {
            Wallets walletSalvo = findByCodigoWallet(codigoWallet);
            walletSalvo.setStatus(ativo);
            repository.save(walletSalvo);
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public Wallets findByCodigoWallet(String codigoWallet) {

        return repository.findByCodigoWallet(codigoWallet).orElseThrow(() -> new WalletNaoEncontradoException(codigoWallet));
    }

    public List<WalletSaldoResponse> getSaldoWalletsList(Long pessoaId) {

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
                "WHERE w.pessoa_id = ? " +
                "GROUP BY w.wallet_id ";


        // Criar uma consulta nativa usando o EntityManager
        Query query = entityManager.createNativeQuery(sql);

        query.setParameter(1, pessoaId);

        // Configurar um transformador para mapear os resultados para a classe RelatorioBasico
        query.unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(WalletSaldoResponse.class));

        // Executar a consulta
        List<WalletSaldoResponse> resultados = query.getResultList();

        return resultados;
    }
}
