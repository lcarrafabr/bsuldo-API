package com.carrafasoft.bsuldo.braviapi.modelo.produtoslist;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProdutosList {

    private List<Indexs> indexes;
    private List<StockList> stocks;
    private List<String> availableSectors;
    private List<String> availableStockTypes;
}
