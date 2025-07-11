package com.carrafasoft.bsuldo.api.v1.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.v1.model.rendavariavel.AcompanhamentoEstrategico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcompanhamentoEstrategicoRepository extends JpaRepository<AcompanhamentoEstrategico, Long> {

    @Query(nativeQuery = true,
    value = "select * from acompanhamneto_estrategico " +
            "where pessoa_id = :pessoaId " +
            "order by acompanhamento_estrategico_id desc")
    List<AcompanhamentoEstrategico> findAllByPessoaId(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select * from acompanhamneto_estrategico " +
            "where pessoa_id = :pessoaId " +
            "and acompanhamento_estrategico_id = :codigo ")
    Optional<AcompanhamentoEstrategico> findByCodigoAndPessoaId(Long codigo, Long pessoaId);

    @Query(nativeQuery = true,
    value = "select count(*) as qtdRepetida " +
            "from acompanhamneto_estrategico " +
            "where ticker = :tiker ")
    Long verificaAcompanhamentoCadastradoByTicker(String tiker);


    @Query(nativeQuery = true,
    value = "SELECT * FROM acompanhamneto_estrategico " +
            "WHERE " +
            "(:ticker IS NOT NULL AND ticker = :ticker) OR " +
            "(:ticker IS NULL AND " +
            "((:setor_id IS NULL OR setor_id = :setor_id) AND " +
            "(:segmento_id IS NULL OR segmento_id = :segmento_id) AND " +
            "(:status_acompanhamento_enum IS NULL OR status_acompanhamento_enum = :status_acompanhamento_enum) AND " +
            "(pessoa_id = :pessoa_id))) ")
    List<AcompanhamentoEstrategico> findByFiltros(String ticker, String setor_id,
                                                  String segmento_id, String status_acompanhamento_enum, Long pessoa_id);
}
