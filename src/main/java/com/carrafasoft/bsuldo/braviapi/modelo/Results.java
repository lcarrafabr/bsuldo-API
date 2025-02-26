package com.carrafasoft.bsuldo.braviapi.modelo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Results {

    private List<Stoks> results;
    private String requestedAt;
    private String took;



}
