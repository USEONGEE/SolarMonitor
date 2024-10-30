package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.AccumulationType;
import com.energy.outsourcing.entity.InverterAccumulation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InverterAccumulationDto {

    private Long inverterId;
    private Double cumulativeEnergy;
    private LocalDateTime date;
    private AccumulationType type;

    public static InverterAccumulationDto fromEntity(InverterAccumulation entity) {
        return new InverterAccumulationDto(
                entity.getInverter().getId(),
                entity.getCumulativeEnergy(),
                entity.getDate(),
                entity.getType()
        );
    }
}
