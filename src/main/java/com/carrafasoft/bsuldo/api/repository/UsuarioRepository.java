package com.carrafasoft.bsuldo.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrafasoft.bsuldo.api.model.Usuarios;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Long>{
	
	public Optional<Usuarios> findByNomeUsuario(String user);

}
