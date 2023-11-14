package com.carrafasoft.bsuldo.api.repository.rendavariavel;

import com.carrafasoft.bsuldo.api.model.rendavariavel.ProdutosRendaVariavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutosRendaVariavelRepository extends JpaRepository<ProdutosRendaVariavel, Long> {
}
