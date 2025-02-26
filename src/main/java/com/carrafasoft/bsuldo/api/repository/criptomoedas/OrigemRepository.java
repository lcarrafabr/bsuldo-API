package com.carrafasoft.bsuldo.api.repository.criptomoedas;

import com.carrafasoft.bsuldo.api.model.criptomoedas.Origens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrigemRepository extends JpaRepository<Origens, Long> {

    @Query(nativeQuery = true,
    value = "select * from origens " +
            "where codigo_origem = :codigoOrigem " +
            "and pessoa_id = :pessoaId ")
    Optional<Origens> findByCodigoOrigemAndPessoaId(@Param("codigoOrigem")String codigoOrigem,
                                                    @Param("pessoaId") Long pessoaId);
}
