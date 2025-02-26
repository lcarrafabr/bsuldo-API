package com.carrafasoft.bsuldo.api.service.serviceImpl.critomoeda;

import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;
import com.carrafasoft.bsuldo.api.model.exceptionmodel.OrigemNaoEncontradaException;
import com.carrafasoft.bsuldo.api.repository.criptomoedas.OrigemRepository;
import com.carrafasoft.bsuldo.api.service.OrigemService;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrigemServiceImpl implements OrigemService {


    @Autowired
    private OrigemRepository repository;

    @Autowired
    private PessoaService pessoaService;



    @Override
    public Origens buscaOrigemPorIdAndToken(String codigoOrigem, String tokenId) {

        return repository.findByCodigoOrigemAndPessoaId(codigoOrigem,
                pessoaService.recuperaIdPessoaByToken(tokenId)).orElseThrow(() -> new OrigemNaoEncontradaException(codigoOrigem));
    }
}
