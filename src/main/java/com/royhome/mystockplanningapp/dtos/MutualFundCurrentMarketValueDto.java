package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutualFundCurrentMarketValueDto {
    private String mutualFundInstrumentName;
    private String planType;
    private Double totalCurrentMarketValue;
    private Double currentNAV;
    private String status;
}
