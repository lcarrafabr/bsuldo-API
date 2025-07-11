package com.carrafasoft.bsuldo.braviapi.service;

import com.carrafasoft.bsuldo.api.v1.model.Indexes;
import com.carrafasoft.bsuldo.braviapi.modelo.PrecoAtualCota;
import com.carrafasoft.bsuldo.braviapi.modelo.Results;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ConsultarProdutoSimples {

    public static  Results consultarProdutoPorTicker(String ticker, String apiToken) {

        StringBuilder jsonCotas = new StringBuilder();
        Results results = new Results();

        try {
            URL url = new URL("https://brapi.dev/api/quote/"+ticker+"?token=" + apiToken); // Use o token lido do application.properties
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String cotas;

            while ((cotas = br.readLine()) != null) {
                jsonCotas.append(cotas);
            }

            results = new Gson().fromJson(jsonCotas.toString(), Results.class);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    public static String retornaSetor(String ticker) {

        StringBuilder jsonCotas = new StringBuilder();
        Indexes indexes = new Indexes();

        try {
            URL url = new URL("https://brapi.dev/api/quote/list?search=" + ticker);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String cotas;

            while ((cotas = br.readLine()) != null) {
                jsonCotas.append(cotas);
            }

            indexes = new Gson().fromJson(jsonCotas.toString(), Indexes.class);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return indexes.getStocks().get(0).getSector();
    }

    public  static PrecoAtualCota buscaPrecoAtualCota(String ticker, String apiToken) {

        StringBuilder jsonCotas = new StringBuilder();
        Results results = new Results();
        PrecoAtualCota precoAtual = new PrecoAtualCota();

        try {
            URL url = new URL("https://brapi.dev/api/quote/"+ticker+"?token=" + apiToken); // Use o token lido do application.properties
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String cotas;

            while ((cotas = br.readLine()) != null) {
                jsonCotas.append(cotas);
            }

            results = new Gson().fromJson(jsonCotas.toString(), Results.class);

            precoAtual.setTicker(ticker);
            precoAtual.setValorAtualCota(results.getResults().get(0).getRegularMarketPrice());

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return precoAtual;
    }

}
