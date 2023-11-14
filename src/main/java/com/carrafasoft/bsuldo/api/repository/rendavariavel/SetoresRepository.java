package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.Setores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetoresRepository extends JpaRepository<Setores, Long> {

    @Query(nativeQuery = true,
            value = "select * from setores "
                    + "where nome_setor LIKE %:nomeSetor% ")
    public Setores buscaPorNomeCategoria(String nomeSetor);


    @Query(nativeQuery = true,
            value = "select * from setores "
                    + "where nome_setor LIKE %:nomeSetor% ")
    public List<Setores> buscaPorNomeSetor(String nomeSetor);
}
