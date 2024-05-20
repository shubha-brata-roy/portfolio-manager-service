package com.royhome.mystockplanningapp.dtos;

import com.royhome.mystockplanningapp.models.mutualfunds.MutualFundCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutualFundInstrumentDto {
    private String categoryName;
    private String mutualFundInstrumentName;
    private String status;
}
