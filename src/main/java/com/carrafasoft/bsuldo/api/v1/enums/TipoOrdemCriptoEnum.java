package com.carrafasoft.bsuldo.api.v1.enums;

public enum TipoOrdemCriptoEnum {

    COMPRA,                          //("Compra de cripto"),            +
    VENDA,                           //("Venda de cripto"),             -
    TRANSFERENCIA_ENTRADA,           //("Transferência recebida"),      +
    TRANSFERENCIA_SAIDA,             //("Transferência enviada"),       -
    AIRDROP,                         //("Recebimento de airdrop"),      +
    HARD_FORK,                       //("Recebimento por hard fork"),   +
    REDENOMINACAO,                   //("Redenominação de token"),      +
    STAKING_RECOMPENSA,              //("Recompensa de staking"),       +
    MINERACAO,                       //("Recompensa de mineração"),     +
    QUEIMA                           //("Queima de tokens");            -
}
