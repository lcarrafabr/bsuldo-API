package com.carrafasoft.bsuldo.api.service;

import com.carrafasoft.bsuldo.api.repository.FeriadoRepository;
import com.carrafasoft.bsuldo.api.utils.FeriadosBrasilUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FeriadosService {

    @Autowired
    private FeriadoRepository repository;

    public Boolean isFeriado(LocalDate dataAtual) {

        Boolean isFeriadoFixo = isFeriadoFixo(dataAtual);

        if(isFeriadoFixo) {
            return true;
        } else {

            return isFeriadoMovel(dataAtual);
        }
    }

    private Boolean isFeriadoFixo(LocalDate dataAtual) {

        Integer dia = dataAtual.getDayOfMonth();
        Integer mes = dataAtual.getMonthValue();

        Integer retorno = repository.pesquisaFeriado(dia,mes);

        return retorno > 0;
    }


    private Boolean isFeriadoMovel(LocalDate dataAtual) {

        return FeriadosBrasilUtils.isFeriado(dataAtual);
    }
}
