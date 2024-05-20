package com.royhome.mystockplanningapp.models.mutualfunds;

import com.royhome.mystockplanningapp.models.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class MutualFundHoldingUnit extends BaseModel {

    @ManyToOne
    private MutualFundInstrument mutualFundInstrument;
    private Double purchasedAmount;
    private Date purchasedDate;

    private Double stampDutyCharges;
}
