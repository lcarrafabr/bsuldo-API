package com.carrafasoft.bsuldo.api.v1.config;

import java.util.Properties;

import com.carrafasoft.bsuldo.api.v1.config.property.BsuldoApiProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
	
	@Autowired
	private BsuldoApiProperty property;
	
	@Bean
	public JavaMailSender javaMailSender() {
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", true);
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.connectiontimeout", 20000);
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    
		mailSender.setJavaMailProperties(props);
	    mailSender.setHost(property.getMail().getHost());
	    mailSender.setPort(property.getMail().getPort());
	    mailSender.setUsername(property.getMail().getUsername());
	    mailSender.setPassword(property.getMail().getPassword());
	    
	    return mailSender;
		
		
	}

}
