package com.example.algamoney.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Profile("oauth-security")
@EnableWebSecurity
@Configuration  // A anotação @EnableWebSecurity já possui a @Configuration. Mas coloco para lembrar que é uma classe de configuração
@EnableResourceServer //<<<< Incluir para implementar o oauth2
@EnableGlobalMethodSecurity(prePostEnabled = true) // <<< Após passar para os usuarios para o banco, habilitar a seguranda dos metodos
//public class ResourceServerConfig extends WebSecurityConfigurerAdapter{  //<< Antes de implementar o oauth2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter{
	
	@Autowired
	private UserDetailsService userDetailsService; //Precisa disso  quando for colocar usuarios no BD e precisa criar uma implementação. (Esta na classe security - Nome: AppUserDetailsService)
	
	
	/**Sobrescrever o metodo configure(AuthenticationManagerBuilder*/
	
	// @Override // <<< Mudar de Override para Autowired
	@Autowired
	//protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	public void configure(AuthenticationManagerBuilder auth) throws Exception { // <<< para usar o oauth2 mudar o metodo para publico
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		
		/**Após implemnetar o usuario no banco de dados não precisa mais da implementação em memoria*/
		//auth.inMemoryAuthentication()
		////.withUser("admin").password("{noop}admin").roles("ROLE");
		//.withUser("admin").password("admin").roles("ROLE");
	}

	/**Sobrescrver o metodo configure(HttpSecurity http)*/
	@Override
	//protected void configure(HttpSecurity http) throws Exception {  // <<< para usar o oauth2 mudar o metodo para publico
	public void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
		.antMatchers("/categorias").permitAll()
		.anyRequest().authenticated()
	.and()
	.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Sem manter sessão
	.and()
	.csrf().disable();
		
		
		/**versão antiga antes do oauth
		
		http.authorizeRequests()
			.antMatchers("/categorias").permitAll()
			.anyRequest().authenticated()
		.and().httpBasic()
		.and()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Sem manter sessão
		.and()
		.csrf().disable();
		
		
		*/
	}
	
	/**Sobrescrever o metodo configure(ResourceServerSecurityConfigurer resources)  quando for usar o oauth*/
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		
		resources.stateless(true);
	}
	
	/**Criado manualmente*/
	public PasswordEncoder passwordEncoder() {
		
		
		return new BCryptPasswordEncoder();
	}
	
	
	/**use esse bean para funcionar a segurança dos metodos com Oauth2*/
	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler() {
		
		return new OAuth2MethodSecurityExpressionHandler();
	}

}
