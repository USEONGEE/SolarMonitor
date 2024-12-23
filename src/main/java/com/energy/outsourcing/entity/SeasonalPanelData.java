package com.energy.outsourcing.entity;

import com.energy.outsourcing.dto.SeasonalPanelDataDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeasonalPanelData extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Double inclinedIrradiance;  // 경사일사량 -> Inclined irradiance
    private Double ambientTemperature; // 외기온도 -> Ambient temperature
    private Double horizontalIrradiance; // 수평일사량 -> Horizontal irradiance
    private Double panelTemperature;   // 모듈온도 -> Panel temperature

    public SeasonalPanelData(SeasonalPanelDataDto seasonalPanelDataDto) {
        this.inclinedIrradiance = seasonalPanelDataDto.getInclinedIrradiance();
        this.ambientTemperature = seasonalPanelDataDto.getAmbientTemperature();
        this.horizontalIrradiance = seasonalPanelDataDto.getHorizontalIrradiance();
        this.panelTemperature = seasonalPanelDataDto.getPanelTemperature();
    }
}