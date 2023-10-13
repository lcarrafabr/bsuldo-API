package com.carrafasoft.bsuldo.api.repository;

import com.carrafasoft.bsuldo.api.model.Categorias;
import com.carrafasoft.bsuldo.api.model.Emissores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmissoresRepository extends JpaRepository<Emissores, Long> {

    @Query(nativeQuery = true,
            value = "select * " +
                    "from emissores " +
                    "where nome_emissor like %:nomeEmissor%")
    public List<Emissores> buscaPorNomeEmissor(String nomeEmissor);


    @Query(nativeQuery = true,
            value = "select * from emissores where status = 1")
    public List<Emissores> buscaEmissoresAtivos();
}
