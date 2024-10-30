package com.energy.outsourcing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AggregatedInverterGenerationDto {

    @Schema(description = "금일 발전량 [Wh]", example = "1000.0")
    private Double todayGeneration;

    @Schema(description = "전일 발전량 [Wh]", example = "950.0")
    private Double previousDayGeneration;

    @Schema(description = "금월 발전량 [Wh]", example = "1100.0")
    private Double monthlyGeneration;

    @Schema(description = "전월 발전량 [Wh]", example = "1050.0")
    private Double previousMonthGeneration;

    @Schema(description = "연간 발전량 [Wh]", example = "2500.0")
    private Double yearlyGeneration;

    @Schema(description = "전년 발전량 [Wh]", example = "2400.0")
    private Double previousYearGeneration;

    @Schema(description = "누적 발전량 [Wh]", example = "500000.0")
    private Double cumulativeGeneration;

    @Schema(description = "전체 전력량 [Wh]", example = "1000.0")
    private Double totalCurrentOutput;
}
