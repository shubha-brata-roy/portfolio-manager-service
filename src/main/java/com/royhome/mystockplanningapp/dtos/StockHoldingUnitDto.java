package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StockHoldingUnitDto {
    private String ownerName;
    private String stockInstrumentName;
    private Date purchasedDate;
    private Double purchasedPrice;
    private Integer purchasedQuantity;
    private Double chargesAndTaxes;
    private String status;
}
