package com.carrafasoft.bsuldo.api.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.carrafasoft.bsuldo.api.model.Lancamentos;
import com.carrafasoft.bsuldo.api.model.Usuarios;
import com.carrafasoft.bsuldo.api.resource.CategoriaResource;

@Component
public class Mailer {
	
	private static final Logger logger = LoggerFactory.getLogger(CategoriaResource.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	
	public void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
		
		
		try {
			helper.setFrom(remetente);
			helper.setTo(destinatarios.toArray(new String[destinatarios.size()]));
			helper.setSubject(assunto);
			helper.setText(mensagem, true);
			
			mailSender.send(mimeMessage);
			
			logger.info("E-mail enviado");
			
		} catch (Exception e) {
			logger.error("**********************   Erro ao enviar e-mail: \n" + e);
		}
		
	}
	
	public void enviarEmailLancamentosVencidos(String remetente, List<String> destinatarios, 
			String assunto, String template, Map<String, Object> variaveis) {
		
		
		Context context = new Context(new Locale("pt", "BR"));
		
		variaveis.entrySet().forEach(e -> context.setVariable(e.getKey(), e.getValue()));
		
		String mensagem  = thymeleaf.process(template, context);
		
		this.enviarEmail(remetente, 
				destinatarios, 
				assunto, 
				mensagem
				);
		
	}
	
	
	
	public void avisarLancamentosVencidos(List<Lancamentos> vencidos, List<Usuarios> destinatarios) {
		
		Map<String, Object> variaveis = new HashMap<String, Object>();
		String template = "mail/aviso-lancamentos-vencidos";
		
		variaveis.put("lancamentos", vencidos);
		
		List<String> emails = destinatarios.stream().map(u -> u.getPessoa().getEmail())
				.collect(Collectors.toList());
		
		this.enviarEmailLancamentosVencidos(
				"bsuldoapi@gmail.com", 
				emails, 
				"Bsuldo-API: Lancamentos vencidos", 
				template, 
				variaveis
				);
	}
	
public void avisarLancamentosPendentes(List<Lancamentos> vencidos, List<Usuarios> destinatarios) {
		
		Map<String, Object> variaveis = new HashMap<String, Object>();
		String template = "mail/aviso-lancamentos-pendentes";
		
		variaveis.put("lancamentos", vencidos);
		
		List<String> emails = destinatarios.stream().map(u -> u.getPessoa().getEmail())
				.collect(Collectors.toList());
		
		this.enviarEmailLancamentosVencidos(
				"bsuldoapi@gmail.com", 
				emails, 
				"Bsuldo-API: Lancamentos pendentes essa semana", 
				template, 
				variaveis
				);
	}
	
	
	
//	public void teste(ApplicationReadyEvent event) {
//	
//	String template = "mail/aviso-lancamentos-vencidos";
//	
//	List<Lancamentos> lista = repo.findAll();
//	
//	Map<String, Object> variaveis = new HashMap<String, Object>();
//	variaveis.put("lancamentos", lista);
//	
//	this.enviarEmailLancamentosVencidos("bsuldoapi@gmail.com", 
//					Arrays.asList("lcarrafa.br@gmail.com", "debora.costasantos@yahoo.com.br"), 
//					"Teste Bsuldo API", 
//					template,
//					variaveis
//					);
//	
//	System.out.println("Email enviado.");
//	
//}

}
