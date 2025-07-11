package com.carrafasoft.bsuldo.api.v1.repository.gerenciamentoapi;

import com.carrafasoft.bsuldo.api.v1.model.gerenciamentoapi.StoksListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface StockListApiRepository extends JpaRepository<StoksListModel, Long> {

    @Query(nativeQuery = true,
    value = "select count(*) as qtd " +
            "from stock_list ")
    Long qtdProdutosList();

    @Query(nativeQuery = true,
    value = "select close from stock_list " +
            "where stock = :ticker ")
    BigDecimal retornaCotacaoFechammento(String ticker);
}
