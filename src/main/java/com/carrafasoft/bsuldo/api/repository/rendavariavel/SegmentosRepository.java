package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.Segmentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentosRepository extends JpaRepository<Segmentos, Long> {

    @Query(nativeQuery = true,
            value = "select * from segmentos "
                    + "where nome_segmento LIKE %:nomeSegmento% ")
    public Segmentos buscaPorNomeSegmento(String nomeSegmento);
}
