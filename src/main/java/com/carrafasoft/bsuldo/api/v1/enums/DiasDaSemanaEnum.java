package com.carrafasoft.bsuldo.api.v1.enums;

public enum DiasDaSemanaEnum {

    MONDAY("Segunda-feira"),
    TUESDAY("Terça-feira"),
    WEDNESDAY("Quarta-feira"),
    THURSDAY("Quinta-feira"),
    FRIDAY("Sexta-feira"),
    SATURDAY("Sábado"),
    SUNDAY("Domingo");

    private final String descricao;

    DiasDaSemanaEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
