package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StockInstrumentDto {
    private String categoryName;
    private String industryName;
    private String stockInstrumentName;
    private String exchange;

    private String status;
}
