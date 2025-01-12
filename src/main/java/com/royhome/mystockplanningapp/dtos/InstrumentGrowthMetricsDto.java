package com.royhome.mystockplanningapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstrumentGrowthMetricsDto {
    private long id;
    private String name;
    private String category;
    private double xirr;
}
