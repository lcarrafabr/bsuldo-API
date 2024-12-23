package com.carrafasoft.bsuldo.api.model.reports;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class GradeTotalMetodoDeCobranca {

    private String metodoCobranca;
    private String jan;
    private String fev;
    private String mar;
    private String abr;
    private String mai;
    private String jun;
    private String jul;
    private String ago;
    private String setembro;
    private String outubro;
    private String nov;
    private String dez;
    private String total;
}
