package com.carrafasoft.bsuldo.braviapi.modelo;

import java.util.List;

public class Results {

    private List<Stoks> results;
    private String requestedAt;
    private String took;


    public List<Stoks> getResults() {
        return results;
    }

    public void setResults(List<Stoks> results) {
        this.results = results;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getTook() {
        return took;
    }

    public void setTook(String took) {
        this.took = took;
    }
}
