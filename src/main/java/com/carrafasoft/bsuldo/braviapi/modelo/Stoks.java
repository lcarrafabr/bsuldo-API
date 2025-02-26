package com.carrafasoft.bsuldo.braviapi.modelo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Stoks {

    private String symbol;
    private String currency;
    private BigDecimal twoHundredDayAverage;
    private BigDecimal twoHundredDayAverageChange;
    private BigDecimal twoHundredDayAverageChangePercent;
    private Double marketCap;
    private String shortName;
    private String longName;
    private BigDecimal regularMarketChange;
    private BigDecimal regularMarketChangePercent;
    private String regularMarketTime;
    private BigDecimal regularMarketPrice;
    private BigDecimal regularMarketDayHigh;
    private String regularMarketDayRange;
    private BigDecimal regularMarketDayLow;
    private Long regularMarketVolume;
    private BigDecimal regularMarketPreviousClose;
    private BigDecimal regularMarketOpen;
    private Long averageDailyVolume3Month;
    private Long averageDailyVolume10Day;
    private BigDecimal fiftyTwoWeekLowChange;
    private BigDecimal fiftyTwoWeekLowChangePercent;
    private String fiftyTwoWeekRange;
    private BigDecimal fiftyTwoWeekHighChange;
    private BigDecimal fiftyTwoWeekHighChangePercent;
    private BigDecimal fiftyTwoWeekLow;
    private BigDecimal fiftyTwoWeekHigh;
    private BigDecimal priceEarnings;
    private BigDecimal earningsPerShare;
    private String logourl;
    private String updatedAt;

}
