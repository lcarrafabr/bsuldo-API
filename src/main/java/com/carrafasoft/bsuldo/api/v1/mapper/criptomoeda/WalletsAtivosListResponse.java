package com.carrafasoft.bsuldo.api.v1.mapper.criptomoeda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletsAtivosListResponse {

    String codigoWallet;
    String nomeWallet;
}
