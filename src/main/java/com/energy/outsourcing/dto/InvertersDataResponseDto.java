package com.energy.outsourcing.dto;

import lombok.Data;

@Data
/**
 * 인버터 다수 조회용 응답 DTO

 */
public class InvertersDataResponseDto {

    private Long inverterId;
    private Double realtimeKw;
    private Double dailyCumulativeKw;

    public InvertersDataResponseDto(Long inverterId, Double realtimeKw, Double dailyCumulativeKw) {
        this.inverterId = inverterId;
        this.realtimeKw = realtimeKw;
        this.dailyCumulativeKw = dailyCumulativeKw;
    }

}

