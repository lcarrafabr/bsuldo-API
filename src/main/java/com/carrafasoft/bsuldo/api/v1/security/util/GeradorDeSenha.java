package com.carrafasoft.bsuldo.api.v1.security.util;

import com.carrafasoft.bsuldo.api.v1.utils.FuncoesUtils;

public class GeradorDeSenha {
	
	public static void main(String[] args) {
		
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//		System.out.println(encoder.encode("admin123"));

		geraUUID();

		//DesdcriptBase64();
		
	}

	private static void geraUUID() {

		String retorno = FuncoesUtils.gerarUUID();

		System.out.println(retorno);
	}

	private static void DesdcriptBase64() {

		String retorno = "YTJiMGZiYTctY2YwYy00OGZjLTg0ZDYtYTZmMDhkYTU1OWE0";

		System.out.println(FuncoesUtils.decryptFromBase64(retorno));
	}

}
