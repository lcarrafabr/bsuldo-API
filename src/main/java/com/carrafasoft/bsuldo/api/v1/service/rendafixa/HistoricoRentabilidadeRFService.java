package com.carrafasoft.bsuldo.api.v1.service.rendafixa;

import com.carrafasoft.bsuldo.api.v1.event.RecursoCriadoEvent;
import com.carrafasoft.bsuldo.api.v1.model.rendafixa.HistoricoRentabilidadeRendaFixa;
import com.carrafasoft.bsuldo.api.v1.repository.rendafixa.HistoricoRentabilidadeRFRepository;
import com.carrafasoft.bsuldo.api.v1.utils.FuncoesUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Service
public class HistoricoRentabilidadeRFService {

    @Autowired
    private HistoricoRentabilidadeRFRepository repository;

    @Autowired
    private ApplicationEventPublisher publisher;


    public ResponseEntity<?> cadastrarHistoricoRendaFixa(
            HistoricoRentabilidadeRendaFixa historico, HttpServletResponse response) {

        BigDecimal rendimento = verificaRendimento(historico);
        String diaDaSemana = FuncoesUtils.retornaDataTextoTraduzido(historico.getDataRentabilidade());

        historico.setRendimento(rendimento);
        historico.setDiaDaSemana(diaDaSemana);

        HistoricoRentabilidadeRendaFixa historicoSalvo = repository.save(historico);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, historicoSalvo.getHistRentabilidadeRFId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(historicoSalvo);
    }

    public HistoricoRentabilidadeRendaFixa atualizarHistoricoRF(Long codigo, HistoricoRentabilidadeRendaFixa historicoRF) {

        HistoricoRentabilidadeRendaFixa historicoSalvo = buscaPorId(codigo);
        BigDecimal rendimento = verificaRendimentoAtualizacao(historicoRF, codigo);
        String diaDaSemana = FuncoesUtils.retornaDataTextoTraduzido(historicoRF.getDataRentabilidade());

        historicoRF.setRendimento(rendimento);
        historicoRF.setDiaDaSemana(diaDaSemana);

        BeanUtils.copyProperties(historicoRF, historicoSalvo, "histRentabilidadeRFId");

        return repository.save(historicoSalvo);
    }

    private BigDecimal verificaRendimento(HistoricoRentabilidadeRendaFixa historico) {

        HistoricoRentabilidadeRendaFixa ultimoRegistro = repository.buscaUltimoRegistro();
        BigDecimal rendimento = BigDecimal.ZERO;
        BigDecimal valorResgateAtual = BigDecimal.ZERO;
        BigDecimal valorResgateAnterior = BigDecimal.ZERO;

        if(ultimoRegistro != null) {

            valorResgateAtual = historico.getValorResgateApp();
            valorResgateAnterior = ultimoRegistro.getValorResgateApp();
            rendimento = valorResgateAtual.subtract(valorResgateAnterior);

        }
        return rendimento;
    }

    private BigDecimal verificaRendimentoAtualizacao(HistoricoRentabilidadeRendaFixa historico, Long codigo) {

        historico.setHistRentabilidadeRFId(codigo);
        HistoricoRentabilidadeRendaFixa ultimoRegistro = repository.buscaRegistroAnterior(historico.getHistRentabilidadeRFId());
        BigDecimal rendimento = BigDecimal.ZERO;
        BigDecimal valorResgateAtual = BigDecimal.ZERO;
        BigDecimal valorResgateAnterior = BigDecimal.ZERO;

        if(ultimoRegistro != null) {

            valorResgateAtual = historico.getValorResgateApp();
            valorResgateAnterior = ultimoRegistro.getValorResgateApp();
            rendimento = valorResgateAtual.subtract(valorResgateAnterior);

        }
        return rendimento;
    }

    private HistoricoRentabilidadeRendaFixa buscaPorId(Long codigo) {

        HistoricoRentabilidadeRendaFixa historicoSalvo = repository.findById(codigo)
                .orElseThrow(() -> new EmptyResultDataAccessException(1));

        return historicoSalvo;
    }


}
