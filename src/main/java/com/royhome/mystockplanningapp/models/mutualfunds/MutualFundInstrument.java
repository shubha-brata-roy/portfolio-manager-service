package com.royhome.mystockplanningapp.models.mutualfunds;

import com.royhome.mystockplanningapp.models.*;
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
    private Category category;

    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    private String folioNumber;

    private Double totalHoldingUnits;
    private Double currentNAV;

    private Double totalAmountInvested;
    private Date totalAmountInvestedUpdatedOn;
    private Double totalCurrentMarketValue;         // ideally totalHoldingUnits * currentNAV
    private Date currentMarketValueUpdatedOn;

    private Double netReturn;
    private Double netReturnRate;
    private Double cagr;
    private Double xirr;
    private Double mwrr;
    private Date growthMetricsUpdatedOn;

    @OneToOne
    private Financials financials;
}
