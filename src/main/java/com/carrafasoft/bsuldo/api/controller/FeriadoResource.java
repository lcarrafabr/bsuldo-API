package com.carrafasoft.bsuldo.api.controller;

import com.carrafasoft.bsuldo.api.model.Feriados;
import com.carrafasoft.bsuldo.api.repository.FeriadoRepository;
import com.carrafasoft.bsuldo.api.service.FeriadosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/feriados")
public class FeriadoResource {

    @Autowired
    private FeriadoRepository repository;

    @Autowired
    private FeriadosService service;

    @GetMapping
    public List<Feriados> feriadosList() {

        return repository.findAll();
    }

    @GetMapping("/is-feriado")
    public Boolean isFeriado() {

        return service.isFeriado(LocalDate.now());
    }


}
