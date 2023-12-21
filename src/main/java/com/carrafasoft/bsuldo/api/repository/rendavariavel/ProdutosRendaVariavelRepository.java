package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.ProdutosRendaVariavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutosRendaVariavelRepository extends JpaRepository<ProdutosRendaVariavel, Long> {

    @Query(nativeQuery = true,
            value = "select * from produtos_renda_variavel "
                    + "where ticker LIKE %:ticker% ")
    public List<ProdutosRendaVariavel> buscaPorNomeProdutoRV(String ticker);
}
