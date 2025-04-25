package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.InverterType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InverterDto {
    private Long id;
    private InverterType inverterType;
}
