package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricsComparisonDto {
    private long id;
    private String name;
    private String category;
    private String subCategory;
    private String planType;
    private double netReturnRate;
    private double xirr;
    private double shareInPortfolioOfTotalInvested;
    private double shareInPortfolioOfTotalMarketValue;
}
