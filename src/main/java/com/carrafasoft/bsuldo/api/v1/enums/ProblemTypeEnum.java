package com.carrafasoft.bsuldo.api.v1.enums;

import lombok.Getter;

@Getter
public enum ProblemTypeEnum {

    RECURSO_NAO_ENCONTRADO("Recurso não encontrado"),
    ENTIDADE_EM_USO("Entidade em uso"),
    ERRO_NEGOCIO("Violação de regra de negócio"),
    MENSAGEM_INCOMPREENSIVEL("Mensagem incompreensivel"),
    PARAMETRO_INVALIDO("Parâmetro inválido"),
    ERRO_DE_SISTEMA("Erro de sistema"),
    DADOS_INVALIDOS("Dados inválidos");

    private String title;

    ProblemTypeEnum(String title) {
        this.title = title;
    }
}
