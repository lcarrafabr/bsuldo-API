package com.carrafasoft.bsuldo.api.repository;

import com.carrafasoft.bsuldo.api.model.Bancos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BancoRepository extends JpaRepository<Bancos, Long> {

    @Query(nativeQuery = true,
    value = "select * from bancos  " +
            "where pessoa_id = :pessoaId ")
    List<Bancos> findAllById(Long pessoaId);
}
