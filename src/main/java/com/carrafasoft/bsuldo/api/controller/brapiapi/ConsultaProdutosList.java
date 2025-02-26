package com.carrafasoft.bsuldo.api.controller.brapiapi;

import com.carrafasoft.bsuldo.api.service.gerenciamentoapi.GerenciamentoProdutosListService;
import com.carrafasoft.bsuldo.braviapi.service.ConsultarListaProdutos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("produtos-list")
public class ConsultaProdutosList {

    @Autowired
    private GerenciamentoProdutosListService service;

    //@Autowired
    private ConsultarListaProdutos consultarListaProdutos;

    @GetMapping("/list")
    public void listaProdutos() {
        //return consultarListaProdutos.ConsultaListaProdutos();

        service.verificaCadastroOuAtualizacaoProdutosList();


    }


}
