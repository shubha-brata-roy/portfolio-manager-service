package com.royhome.mystockplanningapp.dtos;

import com.royhome.mystockplanningapp.models.SubCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MutualFundInstrumentDto {
    private String categoryName;
    private String subCategoryName;
    private String mutualFundInstrumentName;
    private String folioNumber;
    private String status;
    private String planType;
}
