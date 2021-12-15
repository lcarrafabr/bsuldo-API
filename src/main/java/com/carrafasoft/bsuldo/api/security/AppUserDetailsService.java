package com.carrafasoft.bsuldo.api.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.carrafasoft.bsuldo.api.model.Usuarios;
import com.carrafasoft.bsuldo.api.repository.UsuarioRepository;
import com.carrafasoft.bsuldo.api.security.util.UsuarioSistema;

@Service
public class AppUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Optional<Usuarios> usuarioOptional = usuarioRepository.findByNomeUsuario(username);
		Usuarios usuario = usuarioOptional.orElseThrow(() -> new UsernameNotFoundException("Usuario e/ou senha inválidos."));
		
		//return new User(username, usuario.getSenha(), getPermissoes(usuario));
		return new UsuarioSistema(usuario, getPermissoes(usuario));
	}

	private Collection<? extends GrantedAuthority> getPermissoes(Usuarios usuario) {
		
		//Essa condição foi criada para validar o usuario por status tbm, se quebrar para corrigir apagar essa condição
		if(usuario.getStatus().equals(false)) {
			System.out.println("********************************************** INFORMACAO ************************************************");
			System.out.println("Esse erro é quando não tem permissões. Não achei um metodo de corrigir isso.");
			System.out.println("********************************************** ********** ************************************************");
			usuario.setPermissoes(null);
		}
		
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		usuario.getPermissoes().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getDescricao().toUpperCase())));
		
		return authorities;
	}

}
