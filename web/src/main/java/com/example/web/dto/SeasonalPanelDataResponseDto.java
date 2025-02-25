package com.example.web.dto;

import com.example.web.entity.SeasonalPanelData;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SeasonalPanelDataResponseDto {
    private Double horizontalInsolation; // 수평 일사량, 주로 사용됨
    private LocalDateTime timeStamp;

    public static SeasonalPanelDataResponseDto from (SeasonalPanelData seasonalPanelData) {
        SeasonalPanelDataResponseDto dto = new SeasonalPanelDataResponseDto();
        dto.horizontalInsolation = seasonalPanelData.getHorizontalIrradiance();
        dto.timeStamp = seasonalPanelData.getCreatedDate();
        return dto;
    }
}
