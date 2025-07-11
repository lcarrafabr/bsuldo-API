package com.carrafasoft.bsuldo.api.v1.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.v1.model.reports.TotalPorCategoriaMes;

@Repository
public interface TotalPorCategoriaMesRepository{
	
	@Query(nativeQuery = true,
			value = "select c.nome_categoria, sum(l.valor) totais "
					+ "from lancamentos l "
					+ "inner join categorias c on c.categoria_id = l.categoria_id "
					+ "where data_vencimento between :dataIni and :dataFim "
					+ "group by c.nome_categoria ")
	public List<TotalPorCategoriaMes> totalPorCategoriaMes(LocalDate dataIni, LocalDate dataFim);

}
