package com.carrafasoft.bsuldo.api.repository;

import com.carrafasoft.bsuldo.api.model.ProdutoRendaFixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRendaFixaRepository extends JpaRepository<ProdutoRendaFixa, Long> {

    @Query(nativeQuery = true,
            value = "select * " +
                    "from produtos_renda_fixa " +
                    "where status = 1 ")
    List<ProdutoRendaFixa> findProdutorendaFixaAtivo();
}
