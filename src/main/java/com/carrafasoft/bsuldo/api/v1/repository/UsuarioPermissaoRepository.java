package com.carrafasoft.bsuldo.api.v1.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.carrafasoft.bsuldo.api.v1.model.UsuarioPermissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioPermissaoRepository extends JpaRepository<UsuarioPermissao, Long>{
	
	@Query(nativeQuery = true,
			value = "select  * from usuario_permissao where usuario_id = :codigoUser ")
	public List<UsuarioPermissao> pesquisaPermissoesDoUsuario(Long codigoUser);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, 
	value = "delete from usuario_permissao " + 
			"where usuario_id = :usuarioId " +
			"and permissao_id = :permissaoId ")
	public void deletePermissaoByUserAndPermistion(Long usuarioId, Long permissaoId);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, 
	value = "delete from usuario_permissao " + 
			"where usuario_id = :usuarioId ")
	public void deleteAllPermisstionByUser(Long usuarioId);

}
