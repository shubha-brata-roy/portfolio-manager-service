package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HoldingUnitGrowthMetricsDto {
    private long instrumentId;
    private String date;
    private Double amount;
    private String category;
}
