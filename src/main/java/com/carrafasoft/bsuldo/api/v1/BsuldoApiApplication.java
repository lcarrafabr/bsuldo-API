package com.carrafasoft.bsuldo.api.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.carrafasoft.bsuldo.api.v1.config.property.BsuldoApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(BsuldoApiProperty.class)
public class BsuldoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BsuldoApiApplication.class, args);
	}

}
