package com.carrafasoft.bsuldo.api.v1.utils;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

import com.carrafasoft.bsuldo.api.v1.enums.DiasDaSemanaEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sun.el.parser.ParseException;
import org.springframework.util.StringUtils;

public class FuncoesUtils {
	
	
	/**
	 * Função que converte datas de string para LocalDate
	 * Formato yyyy-MM-dd HH:mm:ss
	 @param LocalDate data
	  **/
	public static LocalDateTime converterStringParaLocalDateTime(String data) {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(data, formatter);
		
		return dateTime;
	}
	
	/**
	 * Função que converte datas de string para LocalDate
	 * Formato yyyy-MM-dd HH:mm:ss
	 @param LocalDate data
	  **/
	public static LocalDate converterStringParaLocalDate(String data) {

		LocalDate dateTime = null;

		if(StringUtils.hasLength(data)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			dateTime = LocalDate.parse(data, formatter);
		}
		
		return dateTime;
	}
	
	
	/**
	 * Gera um sequencia de caracteres com 20 digitos aleatoriamente;
	 * 
	 @param LocalDate data
	 * @return 
	  **/
	public static String gerarHash() {
		
		UUID uuid = UUID.randomUUID();
		String myRandom = uuid.toString();
		//System.out.println(myRandom.substring(0,20));
		return myRandom.substring(0,20);
	}


	public static String gerarUUID() {

		UUID uuid = UUID.randomUUID();
		String uuidGerado = uuid.toString();
		return uuidGerado;
	}
	
	public static String getZeroEsq(Long codigo,int quant) throws ParseException, java.text.ParseException{
	      //Calcular zeros
	      String formato = "";

	        for (int i = 0; i < quant; i++) {
	            formato += "0";
	        }

	      //Formatar codigo
	      String codigoRetorno = null;
	      DecimalFormat formatoCodigo = new DecimalFormat(formato);
	      codigoRetorno = formatoCodigo.format(codigo);

	      return codigoRetorno;
	  }
	
	
	/**
	 * Converte SQL DATE em LocalDate
	 * @param Date data - sql date
	 * 
	 * **/
	public static LocalDate converteSQLDateToLocalDate(Date data) {
		
		LocalDate dataConvertida = data.toLocalDate();
		return dataConvertida;
	}
	
	/**
	 * Pega a quantidade total de dias de um mês
	 * @param LocalDate data
	 * 
	 * **/
	public static int quantidadeDiasNoMes(LocalDate data) {

		int diasNoMes = data.lengthOfMonth();
		
		return diasNoMes;
	}
	
	/**
	 * Criptografa o password
	 * @param password password
	 * 
	 * **/
	public static String geraPasswordCrippt(String password) {
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return encoder.encode(password);
	}

	/**
	 * Retorna dia da semana em texto String
	 * @param dataInicial*/
	public static String retornaDataTexto(LocalDate dataInicial) {

		DayOfWeek diaDaSemana = dataInicial.getDayOfWeek();

		return diaDaSemana.toString();
	}

	public static String retornaDataTextoTraduzido(LocalDate dataInicial) {

		DayOfWeek diaDaSemana = dataInicial.getDayOfWeek();
		String diaDaSemanaRetorno = "";

		switch (diaDaSemana) {

			case MONDAY:
				diaDaSemanaRetorno = DiasDaSemanaEnum.MONDAY.getDescricao();
				break;

			case TUESDAY:
				diaDaSemanaRetorno = DiasDaSemanaEnum.TUESDAY.getDescricao();
				break;

			case WEDNESDAY:
				diaDaSemanaRetorno = DiasDaSemanaEnum.WEDNESDAY.getDescricao();
				break;

			case THURSDAY:
				diaDaSemanaRetorno = DiasDaSemanaEnum.THURSDAY.getDescricao();
				break;

			case FRIDAY:
				diaDaSemanaRetorno = DiasDaSemanaEnum.FRIDAY.getDescricao();
				break;

			case SATURDAY:
				diaDaSemanaRetorno = DiasDaSemanaEnum.SATURDAY.getDescricao();
				break;

			case SUNDAY:
				diaDaSemanaRetorno = DiasDaSemanaEnum.SUNDAY.getDescricao();
				break;
		}

		return diaDaSemanaRetorno;
	}

	public static String converteVazioParaNulo(String valor) {

		String retorno = null;

		if(!valor.equals("")) {
			retorno = valor;
		}
		return retorno;
	}

	// Método para criptografar uma string para Base64
	public static String encryptToBase64(String originalString) {
		byte[] encodedBytes = Base64.getEncoder().encode(originalString.getBytes());
		return new String(encodedBytes);
	}

	// Método para descriptografar uma string de Base64 para a string original
	public static String decryptFromBase64(String encryptedString) {
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedString);
			return new String(decodedBytes);
		} catch (IllegalArgumentException e) {
			//System.out.println("Erro ao decodificar a string Base64: " + e.getMessage());
			return null;
		}
	}

	//Verifica se é fim de semana
	public static Boolean fimDeSemanaChecker () {

		LocalDate today = LocalDate.now();
		Boolean fimDeSemana = false;

		// Verificar se é sábado ou domingo
		if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
			fimDeSemana = true;
		}
		return fimDeSemana;
	}

	// Método para verificar se o horário está entre abertura e fim do horario de funcionamento B3
	//true para qualquer horário entre 10:00 e 18:00 (inclusive). OU DADOS DO PARAMETRO
	//false para qualquer horário antes de 10:00 ou depois de 18:00. OU DADOS DO PARAMETRO
	public static boolean estaNoIntervalo(LocalTime horario, String horarioInicio, String horarioFim) {

		LocalTime inicio = LocalTime.parse(horarioInicio);
		LocalTime fim = LocalTime.parse(horarioFim);

		return !horario.isBefore(inicio) && !horario.isAfter(fim);
	}


}


