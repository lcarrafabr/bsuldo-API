package com.carrafasoft.bsuldo.api.v1.repository;

import java.util.List;
import java.util.Optional;

import com.carrafasoft.bsuldo.api.v1.model.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categorias, Long>{


	Optional<Categorias> findByCodigoCategoria(@Param("categoriaCodigo") String categoriaCodigo);

	@Query(nativeQuery = true,
	value = "select * from categorias " +
			"where pessoa_id = :pessoaId ")
	List<Categorias> findAllByPessoaId(@Param("pessoaId") Long pessoaId);

	@Query(nativeQuery = true,
	value = "select * from categorias " +
			"where pessoa_id = :pessoaId " +
			"and codigo_categoria = :codigo ")
	Optional<Categorias> findByIdAndPessoaId(@Param("codigo") String codigo, @Param("pessoaId") Long pessoaId);
	
	@Query(nativeQuery = true,
			value = "select * "
					+ "from categorias "
					+ "where nome_categoria like %:nomeCategoria% " +
					"and pessoa_id = :pessoaId")
	List<Categorias> buscaPorNomeCategoria(@Param("nomeCategoria") String nomeCategoria, @Param("pessoaId") Long pessoaId);
	
	
	@Query(nativeQuery = true,
			value = "select * from categorias where status = 1 " +
					"and pessoa_id = :pessoaId ")
	List<Categorias> buscaCategoriasAtivas(@Param("pessoaId") Long pessoaId);

	void deleteByCodigoCategoria(@Param("codigoCategoria") String codigoCategoria);


}
