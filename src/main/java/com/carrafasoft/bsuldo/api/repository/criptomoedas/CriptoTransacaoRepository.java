package com.carrafasoft.bsuldo.api.repository.criptomoedas;

import com.carrafasoft.bsuldo.api.model.criptomoedas.CriptoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CriptoTransacaoRepository extends JpaRepository<CriptoTransacao, Long> {

    @Query(nativeQuery = true,
    value = "select * from cripto_transacao " +
            "where pessoa_id = :pessoaId ")
    List<CriptoTransacao> findAllByPessoaId(@Param("pessoaId") Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from cripto_transacao " +
            "where pessoa_id = :pessoaId " +
            "and codigo_cripto_transacao = :codigoCriptoTransacao ")
    Optional<CriptoTransacao> findByCodigoCriptoTransacaoAndTokenId(@Param("codigoCriptoTransacao") String codigoCriptoTransacao,
                                                                    @Param("pessoaId") Long pessoaId);
}
