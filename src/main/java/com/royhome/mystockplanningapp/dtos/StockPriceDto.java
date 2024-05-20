package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockPriceDto {
    private String stockInstrumentName;
    private Double currentPrice;
    private String status;
}
