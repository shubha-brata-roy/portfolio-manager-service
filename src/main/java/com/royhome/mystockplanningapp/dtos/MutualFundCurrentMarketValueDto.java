package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutualFundCurrentMarketValueDto {
    private String mutualFundInstrumentName;
    private Double totalCurrentMarketValue;
    private String status;
}
