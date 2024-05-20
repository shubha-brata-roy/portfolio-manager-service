package com.royhome.mystockplanningapp.models.stocks;

import com.royhome.mystockplanningapp.models.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class StockHoldingUnit extends BaseModel {

    @ManyToOne
    private StockInstrument stockInstrument;
    private Date purchasedDate;
    private Double purchasedPrice;
    private Integer purchasedQuantity;
    private Double securitiesTransactionTax;
    private Double nseTransactionCharges;
    private Double sebiCharges;
    private Double stampDutyCharges;
    private Double gst;
    private Double totalCharges;

//    private Double netGrowth;
//    private Double cagr;
//    private Double xirr;
}
