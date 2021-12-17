package com.carrafasoft.bsuldo.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Usuarios;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Long>{
	
	public Optional<Usuarios> findByNomeUsuario(String user);
	
	@Query(nativeQuery = true,
			value = "select u.* "
					+ "from usuarios u "
					+ "inner join usuario_permissao up on up.usuario_id = u.usuario_id "
					+ "inner join permissao p on p.permissao_id = up.permissao_id "
					+ "where p.descricao = 'ROLE_RECEBER_EMAIL'; ")
	public List<Usuarios> getusuariosParaenviarEmail();

}
