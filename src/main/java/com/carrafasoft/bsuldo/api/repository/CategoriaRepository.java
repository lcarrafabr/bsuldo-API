package com.carrafasoft.bsuldo.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Categorias;

@Repository
public interface CategoriaRepository extends JpaRepository<Categorias, Long>{
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from categorias "
					+ "where nome_categoria like %:nomeCategoria% ")
	public List<Categorias> buscaPorNomeCategoria(String nomeCategoria);
	
	
	@Query(nativeQuery = true,
			value = "select * from categorias where status = 1 ")
	public List<Categorias> buscaCategoriasAtivas();


}
