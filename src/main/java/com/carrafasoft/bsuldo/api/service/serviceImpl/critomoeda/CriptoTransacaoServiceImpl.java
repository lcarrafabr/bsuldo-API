package com.carrafasoft.bsuldo.api.service.serviceImpl.critomoeda;

import com.carrafasoft.bsuldo.api.enums.TipoOrdemCriptoEnum;
import com.carrafasoft.bsuldo.api.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.exception.EntidadeNaoEncontradaException;
import com.carrafasoft.bsuldo.api.exception.NegocioException;
import com.carrafasoft.bsuldo.api.mapper.CriptoTransacaoMapper;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.CriptoTransacaoInput;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.CriptoTransacaoResponse;
import com.carrafasoft.bsuldo.api.mapper.criptomoeda.WalletSaldoResponse;
import com.carrafasoft.bsuldo.api.model.Pessoas;
import com.carrafasoft.bsuldo.api.model.criptomoedas.CriptoTransacao;
import com.carrafasoft.bsuldo.api.model.criptomoedas.Wallets;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.CriptoTransacaoNaoEncontradoException;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.CriptoTransacaoRepository;
import com.carrafasoft.bsuldo.api.service.CriptoTransacaoService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.WalletService;
import com.carrafasoft.bsuldo.api.service.validations.CriptoTransacaoValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CriptoTransacaoServiceImpl implements CriptoTransacaoService {

    @Autowired
    private CriptoTransacaoRepository repository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private CriptoTransacaoMapper mapper;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private CriptoTransacaoValidator criptoTransacaoValidator;


    @Override
    public List<CriptoTransacao> listar(String tokenId) {

        Long pessoaId = pessoaService.recuperaIdPessoaByToken(tokenId);

        List<CriptoTransacao> criptoTransacaoList = repository.findAllByPessoaId(pessoaId);
        List<WalletSaldoResponse> saldoWalletList = walletService.getSaldoWalletsList(pessoaId);

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


    @Override
    public CriptoTransacao findByCodigoAndTokenId(String codigoCriptoTransacao, String tokenId) {

        return repository.findByCodigoCriptoTransacaoAndTokenId(codigoCriptoTransacao,
                pessoaService.recuperaIdPessoaByToken(tokenId)).orElseThrow(() -> new CriptoTransacaoNaoEncontradoException(codigoCriptoTransacao));
    }

    @Transactional
    @Override
    public CriptoTransacao cadastrarCriptoTransacao(CriptoTransacaoInput criptoTransacaoInput, String tokenId, HttpServletResponse response) {

        try {
            log.info("...: Preparando cadastro de transação de criptomoeda do usuario: {}", tokenId);

            criptoTransacaoValidator.validar(criptoTransacaoInput);

            Wallets walletSalvo = walletService.findById(criptoTransacaoInput.getWallet().getCodigoWallet(), tokenId);
            Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));

            CriptoTransacao transacaoToSave = mapper.toCriptoTransacaoModel(criptoTransacaoInput, pessoaSalva, walletSalvo);

            CriptoTransacao transacaoSalva = repository.save(transacaoToSave);

            publisher.publishEvent(new RecursoCriadoEvent(this, response, transacaoSalva.getCriptoTransacaoId()));

            log.info("...: Transação Cadastrado com sucesso. :...");

            return transacaoSalva;
        } catch (EntidadeNaoEncontradaException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public CriptoTransacao atualizaCriptoTransacao(String codigoCriptoTransacao, CriptoTransacaoResponse criptoTransacaoInput,
                                                   String tokenId) {

        try {

            criptoTransacaoValidator.validar(validatorImputMapper(criptoTransacaoInput));

            CriptoTransacao transacaoSalva = findByCodigoAndTokenId(codigoCriptoTransacao, tokenId);
            BeanUtils.copyProperties(criptoTransacaoInput, transacaoSalva, "criptoTransacaoId");

            return repository.save(transacaoSalva);

        } catch (CriptoTransacaoNaoEncontradoException e) {
            throw new NegocioException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public void removerCriptoTransacao(String codigoCritoTransacao) {

        try {
            repository.deleteByCodigoCritoTransacao(codigoCritoTransacao);
        } catch (EntidadeNaoEncontradaException e) {
            throw new CriptoTransacaoNaoEncontradoException(codigoCritoTransacao);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeNaoEncontradaException(e.getMessage());
        }
    }


    private CriptoTransacaoInput validatorImputMapper(CriptoTransacaoResponse response) {

        return CriptoTransacaoInput.builder()
                .moeda(response.getMoeda())
                .tipoOrdemCripto(response.getTipoOrdemCripto())
                .valorInvestido(response.getValorInvestido())
                .precoNegociacao(response.getPrecoNegociacao())
                .quantidade(response.getQuantidade())
                .wallet(response.getWallet())
                .build();
    }


}
