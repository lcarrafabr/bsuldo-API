package com.carrafasoft.bsuldo.api.v1.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.Setores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetoresRepository extends JpaRepository<Setores, Long> {

    @Query(nativeQuery = true,
    value = "select * from setores " +
            "where pessoa_id = :pessoaId " +
            "AND (:nomeSetor IS NULL OR LOWER(nome_setor) LIKE LOWER(CONCAT('%', :nomeSetor, '%'))) " +
            "order by setor_id desc ")
    List<Setores> findAllByPessoaId(Long pessoaId, String nomeSetor);

    @Query(nativeQuery = true,
    value = "select * from setores " +
            "where setor_id = :codigo " +
            "and pessoa_id = :pessoaId ")
    Optional<Setores> findByIdAndPessoaId(Long codigo, Long pessoaId);

    @Query(nativeQuery = true,
            value = "select * from setores "
                    + "where nome_setor LIKE %:nomeSetor% " +
                    "and pessoa_id = :pessoaId ")
    public Setores buscaPorNomeCategoria(String nomeSetor, Long pessoaId);


    @Query(nativeQuery = true,
            value = "select * from setores "
                    + "where nome_setor LIKE %:nomeSetor% ")
    public List<Setores> buscaPorNomeSetor(String nomeSetor);

    @Query(nativeQuery = true,
            value = "select * from setores where status = 1 " +
                    "and pessoa_id = :pessoaId ")
    public List<Setores> buscaSetorAtivo(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from setores " +
            "where codigo_setor = :codigoSetor " +
            "and pessoa_id = :pessoaId ")
    Optional<Setores> findByCodigoSetorAndPessoaId(@Param("codigoSetor") String codigoSetor,
                                                   @Param("pessoaId") Long pessoaId);

    @Query(nativeQuery = true,
    value = "delete from setores " +
            "where codigo_setor = :codigo ")
    void deleteByCodigoSetor(@Param("codigo") String codigo);

    Optional<Setores> findByCodigoSetor(@Param("codigo") String codigo);
}
