package com.carrafasoft.bsuldo.api.utils;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.carrafasoft.bsuldo.api.enums.DiasDaSemanaEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sun.el.parser.ParseException;

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
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dateTime = LocalDate.parse(data, formatter);
		
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

}
