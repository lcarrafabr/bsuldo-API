package com.carrafasoft.bsuldo.api.v1.repository;

import com.carrafasoft.bsuldo.api.v1.model.Emissores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmissoresRepository extends JpaRepository<Emissores, Long> {


    @Query(nativeQuery = true,
    value = "select * from emissores " +
            "where pessoa_id = :pessoaId ")
    List<Emissores> findAllByPessoaId(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from emissores " +
            "where emissor_id = :emissorId " +
            "and pessoa_id = :pessoaId ")
    Optional<Emissores> findByIdAndPessoaId(Long emissorId, Long pessoaId);
    @Query(nativeQuery = true,
            value = "select * " +
                    "from emissores " +
                    "where nome_emissor like %:nomeEmissor% " +
                    "and pessoa_id = :pessoaId ")
    List<Emissores> buscaPorNomeEmissor(String nomeEmissor, Long pessoaId);


    @Query(nativeQuery = true,
            value = "select * from emissores where status = 1 " +
                    "and pessoa_id = :pessoaId ")
    List<Emissores> buscaEmissoresAtivos(Long pessoaId);
}
