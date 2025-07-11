package com.carrafasoft.bsuldo.api.v1.security.util;

import java.util.Collection;

import com.carrafasoft.bsuldo.api.v1.model.Usuarios;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UsuarioSistema extends User{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Usuarios usuario;

	public UsuarioSistema(Usuarios usuario, Collection<? extends GrantedAuthority> authorities) {
		super(usuario.getNomeUsuario(), usuario.getSenha(), authorities);
		this.usuario = usuario;
	}

	public Usuarios getUsuario() {
		return usuario;
	}

}
