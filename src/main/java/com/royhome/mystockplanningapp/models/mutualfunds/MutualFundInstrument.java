package com.royhome.mystockplanningapp.models.mutualfunds;

import com.royhome.mystockplanningapp.models.BaseModel;
import com.royhome.mystockplanningapp.models.Financials;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class MutualFundInstrument extends BaseModel {
    private String name;

    @Enumerated(EnumType.STRING)
    private MutualFundCategory category;

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
