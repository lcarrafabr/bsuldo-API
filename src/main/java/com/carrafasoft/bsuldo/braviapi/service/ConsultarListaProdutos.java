package com.carrafasoft.bsuldo.braviapi.service;

import com.carrafasoft.bsuldo.braviapi.modelo.Results;
import com.carrafasoft.bsuldo.braviapi.modelo.produtoslist.ProdutosList;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsultarListaProdutos {

    private static final String LISTA_PRODUTOS_LINK = "https://brapi.dev/api/quote/list";

    public static ProdutosList ConsultaListaProdutos() {
        StringBuilder jsonResponse = new StringBuilder();
        ProdutosList results = new ProdutosList();

        try {
            URL url = new URL(LISTA_PRODUTOS_LINK);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String item;

            while ((item = br.readLine()) != null) {
                jsonResponse.append(item);
            }

            // Verifica se o JSON está vazio
            if (jsonResponse.length() > 0) {
                Gson gson = new Gson();
                results = gson.fromJson(jsonResponse.toString(), ProdutosList.class);
            } else {
                System.out.println("O JSON retornado está vazio.");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return results;
    }
}
