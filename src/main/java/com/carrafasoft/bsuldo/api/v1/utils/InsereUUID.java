//package com.carrafasoft.bsuldo.api.v1.utils;
//
//import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ControleDividendos;
//import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.OrdensDeCompra;
//import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.ControleDividendosRepository;
//import com.carrafasoft.bsuldo.api.v1.repository.rendavariavel.OrdemDeCompraRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class InsereUUID implements CommandLineRunner {
//
//    private final OrdemDeCompraRepository repository;
//
//    public InsereUUID(OrdemDeCompraRepository repository) {
//        this.repository = repository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        List<OrdensDeCompra> controleDividendos = repository.findAll();
//
//        for (OrdensDeCompra divs : controleDividendos) {
//
//            String novoUUID = UUID.randomUUID().toString();
//            repository.atualizarCodigoProdutoRv(divs.getOrdemDeCompraId(), novoUUID);
//
//        }
//
//        System.out.println("Atualizado com sucesso!");
//
//    }
//}
