package com.carrafasoft.bsuldo.api.repository;

import com.carrafasoft.bsuldo.api.model.AvisosAutomaticos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AvisosAutomaticosRepository extends JpaRepository<AvisosAutomaticos, Long> {

    @Query(nativeQuery = true,
    value = "select * from avisos_automaticos " +
            "where pessoa_id = :pessoaId " +
            "order by aviso_automatico_id desc ")
    List<AvisosAutomaticos> findAllByPessoaId(Long pessoaId);

    @Query(nativeQuery = true,
            value = "select * from avisos_automaticos " +
                    "where pessoa_id = :pessoaId " +
                    "and visualizado = 0 ")
    List<AvisosAutomaticos> findAlertasNãoVisualizadosByPessoaId(Long pessoaId);

    @Query(nativeQuery = true,
    value = "select count(*) as qtd from avisos_automaticos " +
            "where pessoa_id = :pessoaId " +
            "and visualizado = 0 ")
    Long quantidadeAlertasByPessoaId(Long pessoaId);


    @Query(nativeQuery = true,
    value = "SELECT count(*) as qtd FROM avisos_automaticos  " +
            "WHERE data_cadastro < CURRENT_DATE() - INTERVAL 30 DAY  " +
            "AND visualizado = 1 ")
    Long verificaSeTemAVisosParaRemover();

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
    value = "delete from avisos_automaticos " +
            "WHERE data_cadastro < CURRENT_DATE() - INTERVAL 30 DAY  " +
            "AND visualizado = 1 ")
    void removerAvisosAutomaticosMaior30Dias();
}
