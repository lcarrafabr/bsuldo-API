package com.carrafasoft.bsuldo.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Categorias;

@Repository
public interface CategoriaRepository extends JpaRepository<Categorias, Long>{


	Optional<Categorias> findByCodigoCategoria(@Param("categoriaCodigo") String categoriaCodigo);

	@Query(nativeQuery = true,
	value = "select * from categorias " +
			"where pessoa_id = :pessoaId ")
	List<Categorias> findAllByPessoaId(Long pessoaId);

	@Query(nativeQuery = true,
	value = "select * from categorias " +
			"where pessoa_id = :pessoaId " +
			"and codigo_categoria = :codigo ")
	Optional<Categorias> findByIdAndPessoaId(String codigo, Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from categorias "
					+ "where nome_categoria like %:nomeCategoria% " +
					"and pessoa_id = :pessoaId")
	List<Categorias> buscaPorNomeCategoria(String nomeCategoria, Long pessoaId);
	
	
	@Query(nativeQuery = true,
			value = "select * from categorias where status = 1 " +
					"and pessoa_id = :pessoaId ")
	List<Categorias> buscaCategoriasAtivas(Long pessoaId);

	void deleteByCodigoCategoria(@Param("codigoCategoria") String codigoCategoria);


}
