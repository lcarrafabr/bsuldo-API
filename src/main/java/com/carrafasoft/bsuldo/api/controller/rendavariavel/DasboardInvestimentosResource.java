package com.carrafasoft.bsuldo.api.controller.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.dto.*;
import com.carrafasoft.bsuldo.api.model.reports.GridProventosRecebidosEFuturos;
import com.carrafasoft.bsuldo.api.service.PessoaService;
import com.carrafasoft.bsuldo.api.service.rendavariavel.dto.DashboardInvestimentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<ValorDividendoRecebidoPorAnoGrid> buscaValoresDivRecebidoGrid(@RequestParam("tokenId") String tokenId,
                                                                              @RequestParam("ano") String ano) {

        return service.buscaValorDividendoReecebidoPorAno(tokenId, ano);
    }

    @GetMapping("/busca-lista-ano-filtro-dividendos-recebidos")
    public List<ListAnoDivsRecebidosFiltroGrid> comboboxAnoFiltroDivsRecebidosGrid(@RequestParam("tokenId") String tokenId) {

        return service.comboboxAnoDivRecebidos(tokenId);
    }

    @GetMapping("/" +
            "historico-proventos-futuros")
    public List<HistoricoProventosFuturos> buscaHistoricoProventosFuturos(@RequestParam("tokenId") String tokenId) {

        List<HistoricoProventosFuturos> response = service.buscaHistoricoProventosFuturos(tokenId);

        return response;
    }

    @GetMapping("/relatorio-por-segmento")
    public List<RelatorioPorSegmento> getRelatorioPorSegmento(@RequestParam("tokenId") String tokenId,
                                                              @RequestParam("tipoProduto") String tipoProduto) {

        return service.getRelatorioPorSegmento(tokenId, tipoProduto);
    }

    @GetMapping("/relatorio-por-setores")
    public List<RelatorioSetores> getRelatorioPorSetor(@RequestParam("tokenId") String tokenId,
                                                       @RequestParam("tipoProduto") String tipoProduto) {

        return service.getRelatorioPorSetores(tokenId, tipoProduto);
    }

    @GetMapping("/grid-proventos-recebidos-e-futuros")
    public List<GridProventosRecebidosEFuturos> getRelatorioGridProventosRecebidosEFuturos(@RequestParam("tokenId") String tokenId,
                                                                                           @RequestParam("tipoPesquisa") String tipoPesquisa) {

        return service.getGridProventosRecebidosEFuturos(tokenId, tipoPesquisa);
    }
}
