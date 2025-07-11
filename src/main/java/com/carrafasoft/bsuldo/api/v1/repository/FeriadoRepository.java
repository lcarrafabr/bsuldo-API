package com.carrafasoft.bsuldo.api.v1.repository;

import com.carrafasoft.bsuldo.api.v1.model.Feriados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeriadoRepository extends JpaRepository<Feriados, Long> {


    @Query(nativeQuery = true,
    value = "select count(*) from feriados " +
            "where dia = :dia " +
            "and mes = :mes ")
    Integer pesquisaFeriado(Integer dia, Integer mes);
}
