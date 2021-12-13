package com.example.algamoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.algamoney.api.config.property.AlgamoneyApiProperty;

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken>{ // << Após implementar o ResponseBodyAdvice<OAuth2AccessToken> mandar implementar os metodos
	
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		
		/**O metodo abaixo beforeBodyWrite somete será acessado quando esse filtro retornar true */
		
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest(); // Convertendo o request para httpservletRequst
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse(); //Convertendo o response para httpServletResponse
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body; //Convertendo o body em DefaultOAuth2AccessToken para conseguir remover o refresh token do body
		
		String refreshToken = body.getRefreshToken().getValue();
		
		adicionarRefreshTokenNoCookie(refreshToken, req, resp); // Metodo criado manualmente para criar o Cookie
		removerRefreshTokenDoBody(token);
		
		return body;
	}

	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);//Esse cookie funcionara apenas em http (sim/não)
		refreshTokenCookie.setSecure(algamoneyApiProperty.getSeguranca().isEnableHttps()); //mudar para true em produção //Deve funcionar apenas em https (sim/não)
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");
		refreshTokenCookie.setMaxAge(2592000);//Quanto tempo para expirar em dias (2592000 = 30 dias)
		
		resp.addCookie(refreshTokenCookie);
	}
	
	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		
		token.setRefreshToken(null);
		
	}

}
