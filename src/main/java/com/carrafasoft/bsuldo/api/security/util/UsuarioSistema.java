package com.carrafasoft.bsuldo.api.security.util;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.carrafasoft.bsuldo.api.model.Usuarios;

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
