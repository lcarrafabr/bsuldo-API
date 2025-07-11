package com.carrafasoft.bsuldo.api.v1.repository.rendafixa;

import com.carrafasoft.bsuldo.api.v1.model.rendafixa.HistoricoRentabilidadeRendaFixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoRentabilidadeRFRepository extends JpaRepository<HistoricoRentabilidadeRendaFixa, Long> {

    @Query(nativeQuery = true,
    value = "SELECT * FROM historico_rentabilidade_rf " +
            "ORDER BY hist_rentabildiade_rf_id DESC ")
    List<HistoricoRentabilidadeRendaFixa> findAllDesc();

    @Query(nativeQuery = true,
    value = "SELECT * FROM historico_rentabilidade_rf " +
            "order by hist_rentabildiade_rf_id desc " +
            "limit 1")
    HistoricoRentabilidadeRendaFixa buscaUltimoRegistro();

    @Query(nativeQuery = true,
            value = "SELECT * FROM historico_rentabilidade_rf " +
                    "WHERE hist_rentabildiade_rf_id < :codigo " +
                    "ORDER BY hist_rentabildiade_rf_id DESC " +
                    "LIMIT 1 ")
    HistoricoRentabilidadeRendaFixa buscaRegistroAnterior(Long codigo);
}
