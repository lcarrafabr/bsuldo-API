package com.carrafasoft.bsuldo.api.v1.service.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.enums.StatusAcompanhamnetoEnum;
import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.model.Pessoas;
import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.AcompanhamentoEstrategico;
import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.AcompanhamentoEstrategicoRepository;
import com.carrafasoft.bsuldo.api.v1.service.PessoaService;
import com.carrafasoft.bsuldo.exceptions.AcompanhamentoEstrategicoExistenteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class AcompanhamentoEstrategicoService {

    @Autowired
    AcompanhamentoEstrategicoRepository repository;

    @Autowired
    OrdemDeCompraRVServiceImpl ordemCompraService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;


    public AcompanhamentoEstrategico cadastrarAcompanhamentoEstrategico(AcompanhamentoEstrategico acompEstr, HttpServletResponse response,
                                                                        String tokenId) throws RuntimeException{

        log.info("...: Iniciando Cadastro de acompanhamento estratégico :...");

        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
        acompEstr.setPessoa(pessoaSalva);

        Boolean acompanhamentoExistente = verificaAcompanhamentoCadastradoByTicker(acompEstr.getTicker());

        if(acompanhamentoExistente) {
            log.info("...:Já existe o acompanhamento com o ticker: {} :...", acompEstr.getTicker());
            log.info("...: CADASTRO CANCELADO :...");
            throw new AcompanhamentoEstrategicoExistenteException("Já existe o acompanhamento com o ticker: " + acompEstr.getTicker());
        }

        Boolean statusAcompVariacaoRecebido = acompEstr.getAcompanharVariacao();
        if(statusAcompVariacaoRecebido && acompEstr.getStatusAcompanhamentoEnum() != StatusAcompanhamnetoEnum.COMPRADO) {

            Boolean retornoVerificacao = verificaTickerEmOrdemDeCompras(acompEstr.getTicker(), acompEstr.getPessoa().getPessoaID());

            if(!retornoVerificacao) {
                acompEstr.setAcompanharVariacao(false);
            }
        }

        AcompanhamentoEstrategico acompEstrSalvo = repository.save(acompEstr);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, acompEstrSalvo.getAcompEstrategicoId()));

        log.info("...: Acompanhamento estratégico cadastrado com sucesso :...");

        return acompEstrSalvo;
    }


    private Boolean verificaAcompanhamentoCadastradoByTicker(String ticker) {

        log.info("...: Iniciando verificação de acompanhamento cadastrado :...");

        Boolean retorno = false;
        Long qtd = repository.verificaAcompanhamentoCadastradoByTicker(ticker.trim());

        if(qtd > 0) {
            retorno = true;
        }
        return retorno;
    }

    private Boolean verificaTickerEmOrdemDeCompras(String ticker, Long pessoaId) {
        log.info("...: Iniciando pesquisa total de ativosComporados :...");
        Boolean retorno = false;
        Long qtdAtivoComprado = ordemCompraService.pesquisaQtdAtivoComprado(ticker, pessoaId);

        if(qtdAtivoComprado == 0) {
            retorno = true;
        }
        log.info("...: Ativo: {} comprado: {} pessoaId: {}", ticker, qtdAtivoComprado, pessoaId);
        return retorno;
    }

    public AcompanhamentoEstrategico atualizaAcompanhamentoEstrategico(Long codigo, AcompanhamentoEstrategico acompEstraRequest, String tokenId) {

        log.info("...: Iniciando atualização de acompanhamento estratégico :...");

        Pessoas pessoaSalva = pessoaService.buscaPessoaPorId(pessoaService.recuperaIdPessoaByToken(tokenId));
        acompEstraRequest.setPessoa(pessoaSalva);

        Boolean statusAcompVariacaoRecebido = acompEstraRequest.getAcompanharVariacao();
        if(statusAcompVariacaoRecebido && acompEstraRequest.getStatusAcompanhamentoEnum() != StatusAcompanhamnetoEnum.COMPRADO) {

            Boolean retornoVerificacao = verificaTickerEmOrdemDeCompras(acompEstraRequest.getTicker(), acompEstraRequest.getPessoa().getPessoaID());

            if(!retornoVerificacao) {
                acompEstraRequest.setAcompanharVariacao(false);
            }
        }

        AcompanhamentoEstrategico acompSalvo = buscaPorId(codigo);
        BeanUtils.copyProperties(acompEstraRequest, acompSalvo, "acompEstrategicoId");

        return repository.save(acompSalvo);
    }

    private AcompanhamentoEstrategico buscaPorId(Long codigo) {

        AcompanhamentoEstrategico acompSalvo = repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
        return acompSalvo;
    }
}
