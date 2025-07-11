package com.carrafasoft.bsuldo.api.v1.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bsuldo")
public class BsuldoApiProperty {

	private final Seguranca seguranca = new Seguranca();
	private final Mail mail = new Mail();

	private String originPermitida = "http://localhost:4200";
	//private String originPermitida = "http://192.168.1.106:4200";

	public Seguranca getSeguranca() {
		return seguranca;
	}

	public String getOriginPermitida() {
		return originPermitida;
	}

	public Mail getMail() {
		return mail;
	}

	public void setOriginPermitida(String originPermitida) {
		this.originPermitida = originPermitida;
	}

	public static class Seguranca {

		private Boolean enableHttps;

		public Boolean getEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(Boolean enableHttps) {
			this.enableHttps = enableHttps;
		}

	}

	public static class Mail {

		private String host;

		private Integer port;

		private String username;

		private String password;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}
		
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	public static class tokemApiBravi {

		private String tokemApiBravi;

		public String getTokemApiBravi() {
			return tokemApiBravi;
		}

		public void setTokemApiBravi(String tokemApiBravi) {
			this.tokemApiBravi = tokemApiBravi;
		}
	}

}
