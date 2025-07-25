package com.carrafasoft.bsuldo.api.v1.service.serviceImpl.critomoeda;

import com.carrafasoft.bsuldo.api.v1.enums.TipoCarteiraEnum;
import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.v1.exception.NegocioException;
import com.carrafasoft.bsuldo.api.v1.mapper.WalletMapper;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.OrigemResponse;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.WalletInput;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.WalletResponse;
import com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda.WalletSaldoResponse;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.Origens;
import com.carrafasoft.bsuldo.api.v1.model.criptomoedas.Wallets;
import com.carrafasoft.bsuldo.api.v1.model.exceptionmodel.WalletNaoEncontradoException;
import com.carrafasoft.bsuldo.api.v1.repository.criptomoedas.WalletRepository;
import com.carrafasoft.bsuldo.api.v1.service.OrigemService;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.api.v1.service.WalletService;
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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    public Wallets atualizarWallet(String codigoWallet, WalletResponse walletInput, String tokenId) {

        try {
            Wallets walletSalvo = findById(codigoWallet, tokenId);
            Origens origemSalva = origemService.findByCodigoOrigem(walletInput.getOrigem().getCodigoOrigem());

            walletSalvo.setOrigem(origemSalva);

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
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
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


    public List<WalletResponse> getListaWalletComSaldo(Long pessoaId) {

        String sql = "SELECT " +
                "w.codigo_wallet as codigoWallet, " +
                "w.nome_carteira as nomeCarteira, " +
                "w.tipo_carteira as tipoCarteira, " +
                "w.status, " +
                "COALESCE(SUM(valor_investido), 0) AS saldo, " +
                "w.data_criacao as dataCriacao, " +
                "w.data_ultima_atualizacao as dataUltimaAtualizacao, " +
                "o.codigo_origem as codigoOrigem, " +  // Pegando detalhes da origem
                "o.nome_origem as nomeOrigem, " +     // Nome da origem
                "o.status_ativo as statusAtivo " +         // Status da origem
                "FROM wallets w " +
                "LEFT JOIN cripto_transacao ct ON w.wallet_id = ct.wallet_id " +
                "LEFT JOIN origens o ON w.origem_id = o.origem_id " +  // Fazendo JOIN com a tabela de origem
                "WHERE w.pessoa_id = ? " +
                "GROUP BY w.wallet_id, o.codigo_origem, o.nome_origem, o.status_ativo ";  // Ajustando GROUP BY

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, pessoaId);

        // Pegando os resultados como lista de Object[]
        List<Object[]> resultList = query.getResultList();
        List<WalletResponse> wallets = new ArrayList<>();

        for (Object[] obj : resultList) {

            OrigemResponse origem = null;
            if (obj[7] != null) {
                origem = OrigemResponse.builder()
                        .codigoOrigem((String) obj[7])
                        .nomeOrigem((String) obj[8])
                        .status((Boolean) obj[9])
                        .build();
            }

            WalletResponse wallet = WalletResponse.builder()
                    .codigoWallet((String) obj[0])
                    .nomeCarteira((String) obj[1])
                    .tipoCarteira(obj[2] != null ? TipoCarteiraEnum.valueOf((String) obj[2]) : null) // Conversão manual
                    .status((Boolean) obj[3])
                    .saldo((BigDecimal) obj[4])
                    .dataCriacao(obj[5] != null ? ((Timestamp) obj[5]).toLocalDateTime() : null)
                    .dataUltimaAtualizacao(obj[6] != null ? ((Timestamp) obj[6]).toLocalDateTime() : null)
                    .origem(origem) // Ajuste conforme necessário
                    .build();

            wallets.add(wallet);
        }

        return wallets;
    }

    @Override
    public List<Wallets> listaWalletsAtivos(String tokenId) {

        return repository.findByTokenId(pessoaService.recuperaIdPessoaByToken(tokenId));
    }

}
