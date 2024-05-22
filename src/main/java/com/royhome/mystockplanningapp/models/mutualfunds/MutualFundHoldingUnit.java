package com.royhome.mystockplanningapp.models.mutualfunds;

import com.royhome.mystockplanningapp.models.BaseModel;
import com.royhome.mystockplanningapp.models.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class MutualFundHoldingUnit extends BaseModel {

    private String isin;
    @ManyToOne
    private MutualFundInstrument mutualFundInstrument;
    private TransactionType transactionType;
    private Long settlementId;
    private Date purchasedDate;
    private Double purchasedAmount;
    private Double purchasedUnits;
    private Double purchasedNAV;
    private Long exchangeOrderId;
    private Double stampDutyCharges;
}
