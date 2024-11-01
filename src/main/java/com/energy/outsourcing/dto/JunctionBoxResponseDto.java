package com.energy.outsourcing.dto;

import com.energy.outsourcing.entity.Inverter;
import com.energy.outsourcing.entity.JunctionBoxChannel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class JunctionBoxResponseDto {
    private Long id;

    private Long inverterId;
}
