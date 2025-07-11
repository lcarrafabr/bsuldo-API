package com.carrafasoft.bsuldo.api.v1.config;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;

@Configuration
public class LocaleConfig {
	
	@PostConstruct
	public void init() {
		
		TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
	}

}
