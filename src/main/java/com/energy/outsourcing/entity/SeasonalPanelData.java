package com.energy.outsourcing.entity;

import com.energy.outsourcing.dto.SeasonalPanelDataDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeasonalPanelData extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double inclinedIrradiance;  // 경사일사량 -> Inclined irradiance
    private Double ambientTemperature; // 외기온도 -> Ambient temperature
    private Double horizontalIrradiance; // 수평일사량 -> Horizontal irradiance
    private Double panelTemperature;   // 모듈온도 -> Panel temperature

    public SeasonalPanelData(SeasonalPanelDataDto seasonalPanelDataDto) {
        this.inclinedIrradiance = seasonalPanelDataDto.getVerticalInsolation();
        this.ambientTemperature = seasonalPanelDataDto.getExternalTemperature();
        this.horizontalIrradiance = seasonalPanelDataDto.getHorizontalInsolation();
        this.panelTemperature = seasonalPanelDataDto.getPanelTemperature();
    }
}