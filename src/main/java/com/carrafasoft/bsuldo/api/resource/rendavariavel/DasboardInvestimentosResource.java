package com.carrafasoft.bsuldo.api.resource.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ValorDividendosRecebidosPorMesEAno;
import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.ValorPorAnoGridDTO;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.rendavariavel.dto.DashboardInvestimentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dashboard-investimentos")
public class DasboardInvestimentosResource {

    @Autowired
    private DashboardInvestimentosService service;

    @Autowired
    private PessoaService pessoaService;

    @GetMapping("/listar-valor-div-recebido-mes-ano")
    public List<ValorDividendosRecebidosPorMesEAno> listarValorDivRecebidoMesAno(@RequestParam("tokenId") String tokenId) {

        List<ValorDividendosRecebidosPorMesEAno> valorrecebido = service.buscaTotalDividiendosPorMesEAno(pessoaService.recuperaIdPessoaByToken(tokenId));

        return valorrecebido;
    }

    @GetMapping("/busca-valor-recebido-div-no-ano")
    public List<ValorPorAnoGridDTO> buscaValoresDivRecebidoGrid(@RequestParam("tokenId") String tokenId) {

        ValorPorAnoGridDTO retorno = service.buscaValorDividendosPorAno(tokenId);
        List<ValorPorAnoGridDTO> retornoList = new ArrayList<>();
        retornoList.add(retorno);

        return retornoList;
    }
}
