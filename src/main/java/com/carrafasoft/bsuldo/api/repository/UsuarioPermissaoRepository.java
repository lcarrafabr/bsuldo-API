package com.carrafasoft.bsuldo.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.UsuarioPermissao;

@Repository
public interface UsuarioPermissaoRepository extends JpaRepository<UsuarioPermissao, Long>{
	
	@Query(nativeQuery = true,
			value = "select  * from usuario_permissao where usuario_id = :codigoUser ")
	public List<UsuarioPermissao> pesquisaPermissoesDoUsuario(Long codigoUser);

}
