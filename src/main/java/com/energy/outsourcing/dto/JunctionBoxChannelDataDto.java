package com.energy.outsourcing.dto;

import lombok.Data;

@Data
public class JunctionBoxChannelDataDto {

    private Integer channelNumber;
    private Double voltage; // 0.1[V] 단위 변환된 값
    private Double current; // 0.01[A] 단위 변환된 값

    // 원시 데이터를 스케일에 맞게 변환하여 DTO로 설정
    public JunctionBoxChannelDataDto(Integer channelNumber, int rawVoltage, int rawCurrent) {
        this.channelNumber = channelNumber;
        this.voltage = rawVoltage * 0.1; // 변환 후 실제 전압
        this.current = rawCurrent * 0.01; // 변환 후 실제 전류
    }
}