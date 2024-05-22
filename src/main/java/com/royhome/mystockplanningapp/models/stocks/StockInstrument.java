package com.royhome.mystockplanningapp.models.stocks;

import com.royhome.mystockplanningapp.models.BaseModel;
import com.royhome.mystockplanningapp.models.Category;
import com.royhome.mystockplanningapp.models.Financials;
import com.royhome.mystockplanningapp.models.Industry;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class StockInstrument extends BaseModel {

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Industry industry;

    private String name;
    private String exchange;
    private Double currentPrice;
    private Date currentPriceUpdatedOn;
    private Integer totalHoldingQuantity;
    private Double averagePurchasedPrice;
    private Double lowestPurchasedPrice;
    private Date lowestPurchasedOn;
    private Double highestPurchasedPrice;
    private Date highestPurchaseOn;
    private Double lastPurchasedPrice;
    private Date lastPurchasedOn;

    private Double totalAmountInvested;
    private Date totalAmountInvestedUpdatedOn;
    private Double totalCurrentMarketValue;
    private Date totalCurrentMarketValueUpdatedOn;

    private Double netReturn;
    private Double netReturnRate;
    private Double cagr;
    private Double xirr;
    private Double mwrr;
    private Date growthMetricsUpdatedOn;

    @OneToOne
    private Financials financials;
}
