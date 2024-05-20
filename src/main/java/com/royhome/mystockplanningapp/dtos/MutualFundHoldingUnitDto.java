package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MutualFundHoldingUnitDto {
    private String mutualFundInstrumentName;
    private Double purchasedAmount;
    private Date purchasedDate;
    private String status;
}
