package com.carrafasoft.bsuldo.api.model.reports;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class GraficoTotalReceitasEDespesasPorAno {

    private Integer mes;
    private String mesName;
    private BigDecimal totalDespesas;
    private BigDecimal totalReceitas;

    // Mapeamento das siglas de inglês para português
    private static final Map<String, String> monthTranslationMap = new HashMap<>();
    static {
        monthTranslationMap.put("Jan", "Jan");
        monthTranslationMap.put("Feb", "Fev");
        monthTranslationMap.put("Mar", "Mar");
        monthTranslationMap.put("Apr", "Abr");
        monthTranslationMap.put("May", "Mai");
        monthTranslationMap.put("Jun", "Jun");
        monthTranslationMap.put("Jul", "Jul");
        monthTranslationMap.put("Aug", "Ago");
        monthTranslationMap.put("Sep", "Set");
        monthTranslationMap.put("Oct", "Out");
        monthTranslationMap.put("Nov", "Nov");
        monthTranslationMap.put("Dec", "Dez");
    }

    // Sobrescrevendo o setter para mesName
    public void setMesName(String mesName) {
        this.mesName = monthTranslationMap.getOrDefault(mesName, mesName);
    }
}
