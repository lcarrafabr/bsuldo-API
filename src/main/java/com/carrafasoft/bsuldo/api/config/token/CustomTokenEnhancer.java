package com.carrafasoft.bsuldo.api.config.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.carrafasoft.bsuldo.api.security.util.UsuarioSistema;

public class CustomTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
		
		Map<String, Object> addInfo = new HashMap<>();
		addInfo.put("nomeUsuario", usuarioSistema.getUsuario().getNomeUsuario());
		addInfo.put("id", usuarioSistema.getUsuario().getPessoa().getPessoaID());
		addInfo.put("nome", usuarioSistema.getUsuario().getPessoa().getNomePessoa());
		
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addInfo);
		return accessToken;
	}

}
