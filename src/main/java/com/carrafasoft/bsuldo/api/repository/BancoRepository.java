package com.carrafasoft.bsuldo.api.repository;

import com.carrafasoft.bsuldo.api.model.Bancos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BancoRepository extends JpaRepository<Bancos, Long> {

    @Query(nativeQuery = true,
    value = "select * from bancos  " +
            "where pessoa_id = :pessoaId " +
            "order by banco_id desc ")
    List<Bancos> findAllById(Long pessoaId);

    @Query(nativeQuery = true,
            value = "select * from bancos  " +
                    "where pessoa_id = :pessoaId " +
                    "and nome_banco like %:nomeBanco% " +
                    "order by banco_id desc ")
    List<Bancos> findAllBynomeBanco(String nomeBanco, Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from bancos " +
            "where banco_id = :bancoId " +
            "and pessoa_id = :pessoaId ")
    Optional<Bancos> findByIdAndPessoaId(Long pessoaId, Long bancoId);
}
