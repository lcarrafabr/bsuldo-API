package com.example.algamoney.api.token;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.catalina.util.ParameterMap;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) //Coloca como prioridade alta pois tem que ser analisado a requisição antes de todas as outras
public class refreshTokenCookiePreProcessorFilter implements Filter{//Implementar o Filter do javax.servlet e mandar criar os implements
	
	/**Somente é necessario o doFilter*/
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request; //Fazer um Cast de request para HttpServletRequest
		
		if("/oauth/token".equalsIgnoreCase(req.getRequestURI()) 
				&& "refresh_token".equals(req.getParameter("grant_type"))
				&& req.getCookies() != null) {
			
			/**Utilizando a api do Java 8*/
			/**
			String refreshToken = 
			        Stream.of(req.getCookies())
			            .filter(cookie -> "refreshToken".equals(cookie.getName()))
			            .findFirst()
			            .map(cookie -> cookie.getValue())
			            .orElse(null);
			
			req = new MyServletRequestWrapper(req, refreshToken);
			*/
			
			/**Usando o for normal*/
			///**
			for(Cookie cookie : req.getCookies()) {
				
				if(cookie.getName().equals("refreshToken")) {
					String refreshToken = cookie.getValue();
					req = new MyServletRequestWrapper(req, refreshToken);
				}
			}
			//*/
			
		}
		
		chain.doFilter(req, response);//Após fazer todos os prodecimentos, continuar o fluxo.

	}
	
	/**Criado manualmente*/
	static class MyServletRequestWrapper extends HttpServletRequestWrapper{ //Após extender o HttpServletRequestWrapper criar o implemento
		
		private String refreshToken;
		
		public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
			super(request);
			
			this.refreshToken = refreshToken;
		}
		
		@Override
		public Map<String, String[]> getParameterMap() { //criar o  o getParameterMap
			
			ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
			map.put("refresh_token", new String[] { refreshToken }); //refresh_token é o nome que o spring irá buscar o token
			map.setLocked(true);//Trava o mapa (não sei para que serve)
			
			return map;
		}
		
	}

	
	
}
