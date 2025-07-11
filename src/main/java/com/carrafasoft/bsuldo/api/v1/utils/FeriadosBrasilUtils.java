package com.carrafasoft.bsuldo.api.v1.utils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FeriadosBrasilUtils {

    // Método para calcular a data da Páscoa para um ano específico
    public static LocalDate calcularPascoa(int ano) {
        int a = ano % 19;
        int b = ano / 100;
        int c = ano % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mes = (h + l - 7 * m + 114) / 31;
        int dia = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(ano, mes, dia);
    }

    public static Set<LocalDate> obterFeriados(int ano) {
        Set<LocalDate> feriados = new HashSet<>();

        // Feriados móveis
        LocalDate pascoa = calcularPascoa(ano);
        feriados.add(pascoa); // Páscoa
        feriados.add(pascoa.minusDays(2)); // Sexta-feira Santa
        feriados.add(pascoa.minusDays(47)); // Carnaval
        feriados.add(pascoa.minusDays(48)); // Carnaval
        feriados.add(pascoa.plusDays(60)); // Corpus Christi

        return feriados;
    }

    // Método para verificar se uma data é feriado
    public static boolean isFeriado(LocalDate data) {
        Set<LocalDate> feriados = obterFeriados(data.getYear());
        return feriados.contains(data);
    }
}
