package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MutualFundHoldingUnitDto {
    private String ownerName;
    private String isin;
    private String mutualFundInstrumentName;
    private String planType;
    private String transactionType;
    private Long settlementId;
    private Date purchasedDate;
    private Double purchasedAmount;
    private Double purchasedUnits;
    private Double purchasedNAV;
    private Long exchangeOrderId;
    private String status;
}
