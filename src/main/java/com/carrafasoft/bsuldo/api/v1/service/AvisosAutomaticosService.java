package com.carrafasoft.bsuldo.api.v1.service;

import com.carrafasoft.bsuldo.api.v1.model.AvisosAutomaticos;
import com.carrafasoft.bsuldo.api.v1.repository.AvisosAutomaticosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class AvisosAutomaticosService {

    @Autowired
    private AvisosAutomaticosRepository repository;

    public void cadastrarAvisoAutomaticoIndividual(AvisosAutomaticos avisoAuto) {

        try {
            log.info("...: Inserindo aviso automático do usuario {} tilulo {} :...", avisoAuto.getPessoa().getPessoaID(), avisoAuto.getTitulo());
            repository.save(avisoAuto);
            log.info("...: Aviso automático cadastrado com sucesso :...");
        } catch (Exception e) {
            log.error("...: ERRO ao cadastrar aviso automático :...");
        }
    }


    public void cadastrarAvisoAutomaticoList(List<AvisosAutomaticos> avisosAutoList) {

        try {
            log.info("...: Iniciando cadastro de avisos automáticos. Data: {}", LocalDate.now());
            for (AvisosAutomaticos avisosAutomaticos : avisosAutoList) {

                repository.save(avisosAutomaticos);
            }
        } catch (Exception e) {
            log.error("...: FALHA ao cadastrar aviso automático: \n", e);
        }
    }

    public void atualizaStatusVisualizado(Long codigo, Boolean visualizado) {

        AvisosAutomaticos avisoAutoSalvo = buscaPorId(codigo);
        Boolean verificaStatusVisualizado = avisoAutoSalvo.getVisualizado();
        if(!verificaStatusVisualizado) {
            avisoAutoSalvo.setVisualizado(visualizado);
            repository.save(avisoAutoSalvo);
        }
    }

    @Scheduled(cron = "${bsuldo.task.schedule.remover_alertas_antigos}")
    public void removerAutomaticoAvisosAcimaDe30Dias() {

        log.info("...: Removendo alertas antigos :...");
        Long qtd = repository.verificaSeTemAVisosParaRemover();

        if(qtd > 0) {
            log.info("...: Encontrado {} alertas antigos :...", qtd);
            repository.removerAvisosAutomaticosMaior30Dias();
            log.info("...: Alertas removidos com sucesso :...");

        } else {
            log.info("...: Não foram contrados alertas para remover :...");
        }

    }

    private AvisosAutomaticos buscaPorId(Long codigo) {

        return repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
    }
}
