package com.carrafasoft.bsuldo.api.v1.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.ProdutosRendaVariavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutosRendaVariavelRepository extends JpaRepository<ProdutosRendaVariavel, Long> {


    @Query(nativeQuery = true,
    value = "select * from produtos_renda_variavel " +
            "where pessoa_id = :pessoaId " +
            "order by produto_id desc ")
    List<ProdutosRendaVariavel> findAllByPessoaId(@Param("pessoaId") Long pessoaId);
    @Query(nativeQuery = true,
            value = "select * from produtos_renda_variavel "
                    + "where ticker LIKE %:ticker% ")
    public List<ProdutosRendaVariavel> buscaPorNomeProdutoRV(String ticker);

    @Query(nativeQuery = true,
    value = "select * from produtos_renda_variavel " +
            "where produto_id = :codigo  " +
            "and pessoa_id = :pessoaId ")
    Optional<ProdutosRendaVariavel> findByIdAndPessoaId(Long codigo, Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from produtos_renda_variavel " +
            "where pessoa_id = :pessoaId " +
            "and codigo_produto_rv = :codigoProdutoRV ")
    Optional<ProdutosRendaVariavel> findByCodigoProdutoRVAndPessoaId(@Param("codigoProdutoRV") String codigoProdutoRV,
                                                                     @Param("pessoaId") Long pessoaId);

    @Query(nativeQuery = true,
            value = "delete from produtos_renda_variavel " +
                    "where codigo_produto_rv = :codigoProdutoRv ")
    void deleteByCodigoProdutoRV(String codigoProdutoRv);
}
