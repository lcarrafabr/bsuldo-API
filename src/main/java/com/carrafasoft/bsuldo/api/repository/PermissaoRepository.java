package com.carrafasoft.bsuldo.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Permissao;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, Long>{
	
	@Query(nativeQuery = true,
			value = "select distinct p.*  "
					+ "from permissao p "
					+ "inner join usuario_permissao up on up.permissao_id = p.permissao_id "
					+ "inner join usuarios u on u.usuario_id = up.usuario_id "
					+ "where u.usuario_id = :codigoUser ")
	public List<Permissao> pegarPermissoesCadastradasDoUsuario(Long codigoUser);

}
