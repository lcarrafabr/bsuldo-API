package com.carrafasoft.bsuldo.api.service.gerenciamentoapi;

import com.carrafasoft.bsuldo.api.model.gerenciamentoapi.StoksListModel;
import com.carrafasoft.bsuldo.api.repository.gerenciamentoapi.StockListApiRepository;
import com.carrafasoft.bsuldo.braviapi.modelo.produtoslist.ProdutosList;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarListaProdutos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class GerenciamentoProdutosListService {

    @Autowired
    private StockListApiRepository repository;

    private ConsultarListaProdutos consultarListaProdutos;

    @Scheduled(cron = "${bsuldo.task.schedule.listar_fechamento_ativos}")
    public void verificaCadastroOuAtualizacaoProdutosList() {

        Long qtdProdutos = repository.qtdProdutosList();

        if(qtdProdutos == 0) {
            cadastrarProdutosList();
            log.info("...: Produtos cadasrados com sucesso.");
        } else {
            atualizaProdutosList();
            log.info("...: Produtos List atualizados com sucesso :...");
        }
    }

    public void cadastrarProdutosList() {

        log.info("Iniciando Cadastro da lista de produtos");

       try {
               ProdutosList produtoResponse = consultarListaProdutos.ConsultaListaProdutos();

               for (int i = 0; i < produtoResponse.getStocks().size(); i++) {

                   StoksListModel stockParaSalvar = new StoksListModel();

                   stockParaSalvar.setStock(produtoResponse.getStocks().get(i).getStock());
                   stockParaSalvar.setName(produtoResponse.getStocks().get(i).getName());
                   stockParaSalvar.setClose(produtoResponse.getStocks().get(i).getClose());

                   if(produtoResponse.getStocks().get(i).getChange() == null) {
                       stockParaSalvar.setChanges(BigDecimal.ZERO);
                   } else {
                       stockParaSalvar.setChanges(produtoResponse.getStocks().get(i).getChange());
                   }

                   stockParaSalvar.setVolume(produtoResponse.getStocks().get(i).getVolume());

                   if(produtoResponse.getStocks().get(i).getMarket_cap() == null) {
                       stockParaSalvar.setMarketCap(BigDecimal.ZERO);
                   } else {
                       stockParaSalvar.setMarketCap(produtoResponse.getStocks().get(i).getMarket_cap());
                   }

                   stockParaSalvar.setLogo(produtoResponse.getStocks().get(i).getLogo());
                   stockParaSalvar.setSector(produtoResponse.getStocks().get(i).getSector());
                   stockParaSalvar.setType(produtoResponse.getStocks().get(i).getType());

                   repository.save(stockParaSalvar);

               }

       } catch (Exception e) {

           log.error("Erro ao cadastrar lista de produtos: {}", e.getMessage());
       }
    }

    public void atualizaProdutosList() {

        try {
            log.info("...: Iniciando atualização de produtos List :...");

                List<StoksListModel> stoksListSalvo = repository.findAll();
                ProdutosList produtoResponse = ConsultarListaProdutos.ConsultaListaProdutos();

                for (int i = 0; i < stoksListSalvo.size(); i++) {

                    StoksListModel stoksListModel = stoksListSalvo.get(i);
                    String stock = stoksListModel.getStock();

                    for (int j = 0; j < produtoResponse.getStocks().size(); j++) {

                        String produtoResponseName = produtoResponse.getStocks().get(j).getStock();

                        if(stock.equalsIgnoreCase(produtoResponseName)) {

                            stoksListModel.setStock(produtoResponse.getStocks().get(j).getStock());
                            stoksListModel.setName(produtoResponse.getStocks().get(j).getName());
                            stoksListModel.setClose(produtoResponse.getStocks().get(j).getClose());

                            if(produtoResponse.getStocks().get(j).getChange() == null) {
                                stoksListModel.setChanges(BigDecimal.ZERO);
                            } else {
                                stoksListModel.setChanges(produtoResponse.getStocks().get(j).getChange());
                            }

                            stoksListModel.setVolume(produtoResponse.getStocks().get(j).getVolume());

                            if(produtoResponse.getStocks().get(j).getMarket_cap() == null) {
                                stoksListModel.setMarketCap(BigDecimal.ZERO);
                            } else {
                                stoksListModel.setMarketCap(produtoResponse.getStocks().get(j).getMarket_cap());
                            }

                            stoksListModel.setLogo(produtoResponse.getStocks().get(j).getLogo());
                            stoksListModel.setSector(produtoResponse.getStocks().get(j).getSector());
                            stoksListModel.setType(produtoResponse.getStocks().get(j).getType());

                            repository.save(stoksListModel);
                        }
                    }

                }
        } catch (Exception e) {

            log.error("...: Erro ao atualizar Produtos List {}", e.getMessage());
        }

    }


    public BigDecimal retornaValorCotacaoFechamento(String ticker) {
        return repository.retornaCotacaoFechammento(ticker);
    }

    private StoksListModel buscaStockListById(Long codigo) {
        return repository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));

    }
}
