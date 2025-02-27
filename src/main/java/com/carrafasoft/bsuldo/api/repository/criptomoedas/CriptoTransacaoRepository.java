package com.carrafasoft.bsuldo.api.repository.criptomoedas;

import com.carrafasoft.bsuldo.api.model.criptomoedas.CriptoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CriptoTransacaoRepository extends JpaRepository<CriptoTransacao, Long> {
}
