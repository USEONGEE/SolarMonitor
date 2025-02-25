package com.example.web.dto;

import lombok.Getter;

@Getter
public class SolarEffectDTO {
    private final double totalGenerationUntilLastMonth;
    private final double thisMonthGeneration;
    private final double co2ReductionUntilLastMonth;
    private final double co2ReductionThisMonth;
    private final double treePlantingEffectUntilLastMonth;
    private final double treePlantingEffectThisMonth;
    private final double oilSavingUntilLastMonth;
    private final double oilSavingThisMonth;

    public SolarEffectDTO(double totalGenerationUntilLastMonth, double thisMonthGeneration,
                          double co2ReductionUntilLastMonth, double co2ReductionThisMonth,
                          double treePlantingEffectUntilLastMonth, double treePlantingEffectThisMonth,
                          double oilSavingUntilLastMonth, double oilSavingThisMonth) {
        this.totalGenerationUntilLastMonth = totalGenerationUntilLastMonth;
        this.thisMonthGeneration = thisMonthGeneration;
        this.co2ReductionUntilLastMonth = co2ReductionUntilLastMonth;
        this.co2ReductionThisMonth = co2ReductionThisMonth;
        this.treePlantingEffectUntilLastMonth = treePlantingEffectUntilLastMonth;
        this.treePlantingEffectThisMonth = treePlantingEffectThisMonth;
        this.oilSavingUntilLastMonth = oilSavingUntilLastMonth;
        this.oilSavingThisMonth = oilSavingThisMonth;
    }
}
