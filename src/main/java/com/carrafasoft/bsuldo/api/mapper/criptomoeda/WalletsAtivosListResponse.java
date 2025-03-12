package com.carrafasoft.bsuldo.api.mapper.criptomoeda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletsAtivosListResponse {

    String codigoWallet;
    String nomeWallet;
}
