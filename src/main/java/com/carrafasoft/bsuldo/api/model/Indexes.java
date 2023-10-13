package com.carrafasoft.bsuldo.api.model;

import java.util.List;

public class Indexes {

    private List<Indices> indexes;
    private List<CotacaoDia> stocks;

    public List<Indices> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Indices> indexes) {
        this.indexes = indexes;
    }

    public List<CotacaoDia> getStocks() {
        return stocks;
    }

    public void setStocks(List<CotacaoDia> stocks) {
        this.stocks = stocks;
    }
}
